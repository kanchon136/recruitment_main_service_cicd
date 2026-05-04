package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionAdditionalRequirementsDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionAdditionalRequirements;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionAdditionalRequirementsParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionAdditionalRequirementsRepository;
import com.cd.recruitment_requisition_service.service.RequisitionAdditionalRequirementsService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.cd.recruitment_requisition_service.utils.PaginatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionAdditionalRequirementsServiceImpl implements RequisitionAdditionalRequirementsService {
    private final RequisitionAdditionalRequirementsRepository requirementsRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;

    public RequisitionAdditionalRequirementsServiceImpl(RequisitionAdditionalRequirementsRepository requirementsRepository,
                                                        RecruitmentRequisitionMasterRepository masterRepository) {
        this.requirementsRepository = requirementsRepository;
        this.masterRepository = masterRepository;
    }

    private RequisitionAdditionalRequirementsDto convertToDto(RequisitionAdditionalRequirements entity) {
         return EntityAndDtoAllMapper.requisitionAdditionalRequirementsEntityToDto(entity);
    }

     private void copyNonNullProperties(RequisitionAdditionalRequirementsParam source, RequisitionAdditionalRequirements target) {
        if (source.getAdditionalRequirement() != null) target.setAdditionalRequirement(source.getAdditionalRequirement());
        if (source.getAdditionalComment() != null) target.setAdditionalComment(source.getAdditionalComment());
    }

    @Override
    public BaseResponse save(RequisitionAdditionalRequirementsParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        RequisitionAdditionalRequirements entity = new RequisitionAdditionalRequirements();
        BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

        entity.setRecruitmentRequisitionMaster(parent);
        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setCreatedBy(param.getActedUserCode());
        entity.setActedUserName(param.getActedUserName());

        RequisitionAdditionalRequirements savedEntity = requirementsRepository.save(entity);

        response.setRequisitionAdditionalRequirementsDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse savedAll(List<RequisitionAdditionalRequirementsParam> params) {
        BaseResponse response = new BaseResponse();

        if (params == null || params.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        List<RequisitionAdditionalRequirements> entitiesToSave = params.stream()
                .map(param -> {
                    RecruitmentRequisitionMaster parent = masterRepository
                            .findById(param.getRecruitmentRequisitionMasterId())
                            .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

                    RequisitionAdditionalRequirements entity = new RequisitionAdditionalRequirements();
                    BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

                    entity.setRecruitmentRequisitionMaster(parent);
                    entity.setRecordStatus(RecordStatus.ACTIVE);
                    entity.setCreatedDate(LocalDate.now());
                    entity.setCreatedDateTime(LocalDateTime.now());
                    entity.setCreatedBy(param.getActedUserCode());
                    entity.setActedUserName(param.getActedUserName());
                    return entity;
                })
                .collect(Collectors.toList());

        List<RequisitionAdditionalRequirements> savedEntities = requirementsRepository.saveAll(entitiesToSave);
        List<RequisitionAdditionalRequirementsDto> dtoList = savedEntities.stream().map(this::convertToDto).collect(Collectors.toList());

        response.setRequisitionAdditionalRequirementsDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionAdditionalRequirementsParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionAdditionalRequirements existing = requirementsRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Additional Requirement not found for ID: " + id));

        copyNonNullProperties(param, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionAdditionalRequirements updatedEntity = requirementsRepository.save(existing);
        response.setRequisitionAdditionalRequirementsDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionAdditionalRequirements existing = requirementsRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Additional Requirement not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
       // existing.setUpdatedBy(upadatedBy);
        requirementsRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionAdditionalRequirements entity = requirementsRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Additional Requirement not found for ID: " + id));

        response.setRequisitionAdditionalRequirementsDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByRequisitionMasterId(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionAdditionalRequirements> entityList = requirementsRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE,masterId);

        List<RequisitionAdditionalRequirementsDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionAdditionalRequirementsDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionAdditionalRequirements> entityList = requirementsRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionAdditionalRequirementsDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionAdditionalRequirementsDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionAdditionalRequirements> entityPage = requirementsRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionAdditionalRequirementsDto> paginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(EntityAndDtoAllMapper::requisitionAdditionalRequirementsEntityToDto));

        response.setRequisitionAdditionalRequirementsDtoPaginatedResponse(paginatedResponse);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
}
