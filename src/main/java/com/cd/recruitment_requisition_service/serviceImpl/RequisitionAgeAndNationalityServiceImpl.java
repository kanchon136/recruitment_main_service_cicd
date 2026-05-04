package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionAgeAndNationalityDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionAgeAndNationality;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionAgeAndNationalityParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionAgeAndNationalityRepository;
import com.cd.recruitment_requisition_service.service.RequisitionAgeAndNationalityService;
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
public class RequisitionAgeAndNationalityServiceImpl implements RequisitionAgeAndNationalityService {
    private final RequisitionAgeAndNationalityRepository nationalityRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;

    public RequisitionAgeAndNationalityServiceImpl(RequisitionAgeAndNationalityRepository nationalityRepository,
                                                   RecruitmentRequisitionMasterRepository masterRepository) {
        this.nationalityRepository = nationalityRepository;
        this.masterRepository = masterRepository;
    }

    private RequisitionAgeAndNationalityDto convertToDto(RequisitionAgeAndNationality entity) {
        return EntityAndDtoAllMapper.requisitionAgeAndNationalityEntityToDto(entity);
    }


    @Override
    public BaseResponse save(RequisitionAgeAndNationalityParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        RequisitionAgeAndNationality entity = new RequisitionAgeAndNationality();
        BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

        entity.setRecruitmentRequisitionMaster(parent);

        entity.setRecordStatus(RecordStatus.ACTIVE);
        // entity.setCreatedBy(currentUserId);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setCreatedBy(param.getActedUserCode());
        entity.setActedUserName(param.getActedUserName());

        RequisitionAgeAndNationality savedEntity = nationalityRepository.save(entity);

        response.setRequisitionAgeAndNationalityDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse savedAll(List<RequisitionAgeAndNationalityParam> requisitionAgeAndNationalityParams) {
        BaseResponse response = new BaseResponse();

        if (requisitionAgeAndNationalityParams == null || requisitionAgeAndNationalityParams.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        List<RequisitionAgeAndNationality> entitiesToSave = requisitionAgeAndNationalityParams.stream()
                .map(param -> {
                    // 1. Validate Parent Requisition Master for each
                    RecruitmentRequisitionMaster parent = masterRepository
                            .findById(param.getRecruitmentRequisitionMasterId())
                            .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

                    RequisitionAgeAndNationality entity = new RequisitionAgeAndNationality();
                    BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

                    entity.setRecruitmentRequisitionMaster(parent);

                    entity.setRecordStatus(RecordStatus.ACTIVE);
                    entity.setCreatedDate(LocalDate.now());
                    entity.setCreatedDateTime(LocalDateTime.now());
                    entity.setCreatedBy(param.getActedUserCode());
                    entity.setActedUserName(parent.getActedUserName());
                    return entity;
                })
                .collect(Collectors.toList());

        // 3. Save All
        List<RequisitionAgeAndNationality> savedEntities = nationalityRepository.saveAll(entitiesToSave);
        List<RequisitionAgeAndNationalityDto> dtoList = savedEntities.stream().map(this::convertToDto).collect(Collectors.toList());

        response.setRequisitionAgeAndNationalityDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse update(Long id, RequisitionAgeAndNationalityParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionAgeAndNationality existing = nationalityRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Age And Nationality not found for ID: " + id));


        copyNonNullProperties(param, existing);

        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionAgeAndNationality updatedEntity = nationalityRepository.save(existing);
        response.setRequisitionAgeAndNationalityDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionAgeAndNationality existing = nationalityRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Age And Nationality not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        nationalityRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionAgeAndNationality entity = nationalityRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Age And Nationality not found for ID: " + id));

        response.setRequisitionAgeAndNationalityDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse findAllByParentId(Long parentId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionAgeAndNationality> entityList = nationalityRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, parentId);

        List<RequisitionAgeAndNationalityDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionAgeAndNationalityDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionAgeAndNationality> entityList = nationalityRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionAgeAndNationalityDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionAgeAndNationalityDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionAgeAndNationality> entityPage = nationalityRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionAgeAndNationalityDto> paginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(EntityAndDtoAllMapper::requisitionAgeAndNationalityEntityToDto));

        response.setRequisitionAgeAndNationalityDtoPaginatedResponse(paginatedResponse);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RequisitionAgeAndNationalityParam source, RequisitionAgeAndNationality target) {
        if (source.getNationality() != null) target.setNationality(source.getNationality());
        if (source.getAgePreferenceFrom() != null) target.setAgePreferenceFrom(source.getAgePreferenceFrom());
        if (source.getAgePreferenceTo() != null) target.setAgePreferenceTo(source.getAgePreferenceTo());
        if (source.getAdditionalRequirement() != null)
            target.setAdditionalRequirement(source.getAdditionalRequirement());
        if (source.getAdditionalComment() != null) target.setAdditionalComment(source.getAdditionalComment());
    }
}
