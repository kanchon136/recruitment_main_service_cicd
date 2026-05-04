package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionJobTaskDto;
import com.cd.recruitment_requisition_service.entity.RequisitionJobDescription;
import com.cd.recruitment_requisition_service.entity.RequisitionJobTask;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionJobTaskParam;
import com.cd.recruitment_requisition_service.repository.RequisitionJobDescriptionRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionJobTaskRepository;
import com.cd.recruitment_requisition_service.service.RequisitionJobTaskService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.cd.recruitment_requisition_service.utils.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionJobTaskRepositoryServiceImpl implements RequisitionJobTaskService {
    private final RequisitionJobTaskRepository jobTaskRepository;
    private final RequisitionJobDescriptionRepository jobDescriptionRepository; // For fetching parent

     public RequisitionJobTaskRepositoryServiceImpl(RequisitionJobTaskRepository jobTaskRepository,
                                                    RequisitionJobDescriptionRepository jobDescriptionRepository) {
        this.jobTaskRepository = jobTaskRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    @Override
    @Transactional
    public BaseResponse findAllByRecordStatusAndRequisitionJobDescription_idAndRecordStatus(Long parentId) {
        BaseResponse response = new BaseResponse();

         List<RequisitionJobTask> list = jobTaskRepository
                .findAllByRecordStatusAndRequisitionJobList_RequisitionJobDescription_IdAndRecordStatus(RecordStatus.ACTIVE, parentId,RecordStatus.ACTIVE);

        List<RequisitionJobTaskDto> dtoList = list.stream()
                .map(EntityAndDtoAllMapper::jobTaskEntityToDto)
                .collect(Collectors.toList());

         response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse save(RequisitionJobTaskParam param) {
        BaseResponse response = new BaseResponse();

//         RequisitionJobTask entity = new RequisitionJobTask();
//         BeanUtils.copyProperties(param, entity);
//
//         RequisitionJobDescription parent = jobDescriptionRepository
//                .findByIdAndRecordStatus(param.getRequisitionJobDescriptionId(), RecordStatus.ACTIVE)
//                .orElseThrow(() -> new CustomException("Active Requisition Job Description not found for ID: " + param.getRequisitionJobDescriptionId()));
//
//         entity.setRequisitionJobDescription(parent);
//         entity.setRecordStatus(RecordStatus.ACTIVE);
//         entity.setCreatedDate(LocalDate.now());
//         entity.setCreatedDateTime(LocalDateTime.now());
//         entity.setCreatedBy(param.getActedUserCode());
//         entity.setActedUserName(param.getActedUserName());
//
//         RequisitionJobTask savedEntity = jobTaskRepository.save(entity);
//
//         RequisitionJobTaskDto dto = EntityAndDtoAllMapper.jobTaskEntityToDto(savedEntity);
//         response.setRequisitionJobTaskDto(dto); // Placeholder DTO setter
//        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionJobTaskParam param) {
        BaseResponse response = new BaseResponse();
//        Optional<RequisitionJobTask> optional = jobTaskRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
//        if (optional.isEmpty()) {
//            response.setMessage("Requisition Job Task not found for ID: " + id);
//            return response;
//        }
//
//        RequisitionJobTask existing = optional.get();
//        copyNonNullProperties(param, existing);
//
//         if (param.getRequisitionJobDescriptionId() != null) {
//            jobDescriptionRepository.findByIdAndRecordStatus(param.getRequisitionJobDescriptionId(), RecordStatus.ACTIVE)
//                    .ifPresent(existing::setRequisitionJobDescription);
//        }
//
//       //  existing.setUpdatedBy(currentUserId);
//        existing.setUpdatedAt(LocalDateTime.now());
//         existing.setUpdatedBy(param.getUpdatedBy());
//
//        RequisitionJobTask updated = jobTaskRepository.save(existing);
//
//        RequisitionJobTaskDto dto = EntityAndDtoAllMapper.jobTaskEntityToDto(updated);
//         response.setRequisitionJobTaskDto(dto); // Placeholder DTO setter
//        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionJobTask> optional = jobTaskRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Job Task not found for ID: " + id);
            return response;
        }

        RequisitionJobTask existing = optional.get();
         existing.setRecordStatus(RecordStatus.DELETED);
       // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        jobTaskRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionJobTask> optional = jobTaskRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Job Task not found for ID: " + id);
            return response;
        }

        RequisitionJobTaskDto dto = EntityAndDtoAllMapper.jobTaskEntityToDto(optional.get());
         response.setRequisitionJobTaskDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdDateTime").descending());
        Page<RequisitionJobTask> page = jobTaskRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionJobTaskDto> pagination =
                PaginatedResponse.fromPage(page.map(EntityAndDtoAllMapper::jobTaskEntityToDto));

        BaseResponse response = new BaseResponse();
        response.setRequisitionJobTaskDtoPaginatedResponse(pagination); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAll() {
        var list = jobTaskRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
        BaseResponse response = new BaseResponse();
         response.setRequisitionJobTaskDtos(list.stream() // Placeholder DTO setter
                 .map(EntityAndDtoAllMapper::jobTaskEntityToDto)
                 .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

     private void copyNonNullProperties(RequisitionJobTaskParam source, RequisitionJobTask target) {
         if (source.getJobTask() != null) target.setJobTask(source.getJobTask());
    }
}
