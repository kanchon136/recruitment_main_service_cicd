package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionJobDescriptionDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionJobDescription;
import com.cd.recruitment_requisition_service.entity.RequisitionJobList;
import com.cd.recruitment_requisition_service.entity.RequisitionJobTask;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionJobDescriptionParam;
import com.cd.recruitment_requisition_service.param.RequisitionJobListParam;
import com.cd.recruitment_requisition_service.param.RequisitionJobTaskParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionJobDescriptionRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionJobListRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionJobTaskRepository;
import com.cd.recruitment_requisition_service.service.RequisitionJobDescriptionService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionJobDescriptionServiceImpl implements RequisitionJobDescriptionService {

    private final RequisitionJobDescriptionRepository jobDescriptionRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;
    private final RequisitionJobListRepository requisitionJobListRepository;
    private final RequisitionJobTaskRepository  requisitionJobTaskRepository;

    public RequisitionJobDescriptionServiceImpl(RequisitionJobDescriptionRepository jobDescriptionRepository,
                                                RecruitmentRequisitionMasterRepository masterRepository,
                                                RequisitionJobListRepository requisitionJobListRepository,
                                                RequisitionJobTaskRepository  requisitionJobTaskRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.masterRepository = masterRepository;
        this.requisitionJobListRepository = requisitionJobListRepository;
        this.requisitionJobTaskRepository =  requisitionJobTaskRepository;
    }

    @Override
    @Transactional
    public BaseResponse save(RequisitionJobDescriptionParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parent = masterRepository.findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Active Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        // Level 1: Job Description
        RequisitionJobDescription descriptionEntity = new RequisitionJobDescription();
        BeanUtils.copyProperties(param, descriptionEntity, "requisitionJobListParams");
        descriptionEntity.setRecruitmentRequisitionMaster(parent);
        setBaseFieldsForSave(descriptionEntity, param.getActedUserCode(), param.getActedUserName());

        // Level 2 & 3: Job List and Tasks
        if (param.getRequisitionJobListParams() != null && !param.getRequisitionJobListParams().isEmpty()) {
            List<RequisitionJobList> listEntities = param.getRequisitionJobListParams().stream().map(listParam -> {
                RequisitionJobList jobList = new RequisitionJobList();
                BeanUtils.copyProperties(listParam, jobList, "requisitionJobTaskParams");
                jobList.setRequisitionJobDescription(descriptionEntity);
                setBaseFieldsForSave(jobList, param.getActedUserCode(), param.getActedUserName());

                if (listParam.getRequisitionJobTaskParams() != null && !listParam.getRequisitionJobTaskParams().isEmpty()) {
                    List<RequisitionJobTask> taskEntities = listParam.getRequisitionJobTaskParams().stream().map(taskParam -> {
                        RequisitionJobTask task = new RequisitionJobTask();
                        BeanUtils.copyProperties(taskParam, task);
                        task.setRequisitionJobList(jobList);
                        setBaseFieldsForSave(task, param.getActedUserCode(), param.getActedUserName());
                        return task;
                    }).collect(Collectors.toList());
                    jobList.setTasks(taskEntities);
                }
                return jobList;
            }).collect(Collectors.toList());
            descriptionEntity.setRequisitionJobLists(listEntities);
        }

        RequisitionJobDescription savedEntity = jobDescriptionRepository.save(descriptionEntity);
        response.setRequisitionJobDescriptionDto(EntityAndDtoAllMapper.jobDescriptionEntityToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    @Transactional
    public BaseResponse update(Long id, RequisitionJobDescriptionParam param) {
        BaseResponse response = new BaseResponse();

        // 1. Fetch existing Parent Description
        RequisitionJobDescription existingDescription = jobDescriptionRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Job Description not found for ID: " + id));

        // 2. Update Description Level
        copyNonNullProperties(param, existingDescription, "requisitionJobListParams", "recruitmentRequisitionMasterId");
        existingDescription.setUpdatedAt(LocalDateTime.now());
        existingDescription.setUpdatedBy(param.getActedUserCode());

        // 3. Update Master Relation if provided
        if (param.getRecruitmentRequisitionMasterId() != null) {
            masterRepository.findByIdAndRecordStatus(param.getRecruitmentRequisitionMasterId(), RecordStatus.ACTIVE)
                    .ifPresent(existingDescription::setRecruitmentRequisitionMaster);
        }

        // 4. Hierarchical Update for Lists
        if (param.getRequisitionJobListParams() != null) {
            for (RequisitionJobListParam listParam : param.getRequisitionJobListParams()) {
                RequisitionJobList jobList;

                if (listParam.getId() != null) {
                    // UPDATE: Find existing list in the current collection
                    jobList = existingDescription.getRequisitionJobLists().stream()
                            .filter(l -> l.getId().equals(listParam.getId()))
                            .findFirst()
                            .orElseThrow(() -> new CustomException("Job List ID " + listParam.getId() + " not found in this Description"));

                    copyNonNullProperties(listParam, jobList, "requisitionJobTaskParams");
                    jobList.setUpdatedAt(LocalDateTime.now());
                    jobList.setUpdatedBy(param.getActedUserCode());
                } else {
                    // INSERT: Create new row if no ID is provided
                    jobList = new RequisitionJobList();
                    copyNonNullProperties(listParam, jobList, "requisitionJobTaskParams");
                    jobList.setRequisitionJobDescription(existingDescription);
                    // Custom helper to set createdBy, createdDate, recordStatus etc.
                    setBaseFieldsForSave(jobList, param.getActedUserCode(), param.getActedUserName());
                    existingDescription.getRequisitionJobLists().add(jobList);
                }

                // 5. Hierarchical Update for Tasks inside the List
                if (listParam.getRequisitionJobTaskParams() != null) {
                    for (RequisitionJobTaskParam taskParam : listParam.getRequisitionJobTaskParams()) {
                        if (taskParam.getId() != null) {
                            // UPDATE: Find existing task
                            RequisitionJobTask existingTask = jobList.getTasks().stream()
                                    .filter(t -> t.getId().equals(taskParam.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new CustomException("Task ID " + taskParam.getId() + " not found in List"));

                            copyNonNullProperties(taskParam, existingTask);
                            existingTask.setUpdatedAt(LocalDateTime.now());
                            existingTask.setUpdatedBy(param.getActedUserCode());
                        } else {
                            // INSERT: Create new task row
                            RequisitionJobTask newTask = new RequisitionJobTask();
                            copyNonNullProperties(taskParam, newTask);
                            newTask.setRequisitionJobList(jobList);
                            setBaseFieldsForSave(newTask, param.getActedUserCode(), param.getActedUserName());
                            jobList.getTasks().add(newTask);
                        }
                    }
                }
            }
        }

        RequisitionJobDescription updated = jobDescriptionRepository.save(existingDescription);
        response.setRequisitionJobDescriptionDto(EntityAndDtoAllMapper.jobDescriptionEntityToDto(updated));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
    @Override
    @Transactional
    public BaseResponse saveAll(List<RequisitionJobDescriptionParam> jobDescriptionParamList) {
        BaseResponse response = new BaseResponse();
        if (jobDescriptionParamList == null || jobDescriptionParamList.isEmpty()) {
            throw new CustomException("Input list cannot be empty.");
        }

        List<RequisitionJobDescriptionDto> savedDtos = jobDescriptionParamList.stream()
                .map(param -> (RequisitionJobDescriptionDto) save(param).getRequisitionJobDescriptionDto())
                .collect(Collectors.toList());

        response.setRequisitionJobDescriptionDtos(savedDtos);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();
        RequisitionJobDescription existing = jobDescriptionRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Job Description not found for ID: " + id));

        // Soft Delete for all 3 levels
        existing.setRecordStatus(RecordStatus.DELETED);
        existing.setUpdatedAt(LocalDateTime.now());

        existing.getRequisitionJobLists().forEach(list -> {
            list.setRecordStatus(RecordStatus.DELETED);
            list.setUpdatedAt(LocalDateTime.now());
            list.getTasks().forEach(task -> {
                task.setRecordStatus(RecordStatus.DELETED);
                task.setUpdatedAt(LocalDateTime.now());
            });
        });

        jobDescriptionRepository.save(existing);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse deleteByIds(Long descriptionId, Long listId, Long taskId) {
        BaseResponse response = new BaseResponse();
        LocalDateTime now = LocalDateTime.now();

        // 1. Delete specific Task if taskId is provided
        if (taskId != null) {
            RequisitionJobTask task = requisitionJobTaskRepository.findByIdAndRecordStatus(taskId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Job Task not found for ID: " + taskId));

            task.setRecordStatus(RecordStatus.DELETED);
            task.setUpdatedAt(now);
            requisitionJobTaskRepository.save(task);
            response.setMessage("Task deleted successfully.");
        }
        // 2. Delete specific List and its Tasks if listId is provided
        else if (listId != null) {
            RequisitionJobList jobList = requisitionJobListRepository.findByIdAndRecordStatus(listId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Job List not found for ID: " + listId));

            jobList.setRecordStatus(RecordStatus.DELETED);
            jobList.setUpdatedAt(now);
            jobList.getTasks().forEach(t -> {
                t.setRecordStatus(RecordStatus.DELETED);
                t.setUpdatedAt(now);
            });
            requisitionJobListRepository.save(jobList);
            response.setMessage("Job List and its tasks deleted successfully.");
        }
        // 3. Delete Description and everything under it if only descriptionId is provided
        else if (descriptionId != null) {
            RequisitionJobDescription description = jobDescriptionRepository.findByIdAndRecordStatus(descriptionId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Job Description not found for ID: " + descriptionId));

            description.setRecordStatus(RecordStatus.DELETED);
            description.setUpdatedAt(now);

            description.getRequisitionJobLists().forEach(list -> {
                list.setRecordStatus(RecordStatus.DELETED);
                list.setUpdatedAt(now);
                list.getTasks().forEach(task -> {
                    task.setRecordStatus(RecordStatus.DELETED);
                    task.setUpdatedAt(now);
                });
            });
            jobDescriptionRepository.save(description);
            response.setMessage("Job Description and all associated data deleted successfully.");
        }
        else {
            throw new CustomException("No valid ID provided for deletion.");
        }

        return response;
    }

    @Override
    @Transactional
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();
        RequisitionJobDescription entity = jobDescriptionRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Job Description not found for ID: " + id));
        response.setRequisitionJobDescriptionDto(EntityAndDtoAllMapper.jobDescriptionEntityToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAll() {
        List<RequisitionJobDescription> list = jobDescriptionRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
        BaseResponse response = new BaseResponse();
        response.setRequisitionJobDescriptionDtos(list.stream().map(EntityAndDtoAllMapper::jobDescriptionEntityToDto).toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentMasterId) {
        BaseResponse response = new BaseResponse();
        List<RequisitionJobDescription> list = jobDescriptionRepository.findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, parentMasterId);
        response.setRequisitionJobDescriptionDtos(list.stream().map(EntityAndDtoAllMapper::jobDescriptionEntityToDto).collect(Collectors.toList()));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    // Utility to set common fields
    private void setBaseFieldsForSave(Object entity, String userCode, String userName) {
        if (entity instanceof com.cd.recruitment_requisition_service.entity.BaseEntity base) {
            base.setRecordStatus(RecordStatus.ACTIVE);
            base.setCreatedDate(LocalDate.now());
            base.setCreatedDateTime(LocalDateTime.now());
            base.setCreatedBy(userCode);
            base.setActedUserName(userName);
        }
    }

    @Override
    @Transactional
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        // Implement logic if needed
        return null;
    }

    private void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));

         final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreSet.add(pd.getName());
            }
        }

         BeanUtils.copyProperties(source, target, ignoreSet.toArray(new String[0]));
    }

}
