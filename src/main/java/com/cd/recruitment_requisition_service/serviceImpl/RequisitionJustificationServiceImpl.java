package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionJustificationDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionJustification;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionJustificationParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionJustificationRepository;
import com.cd.recruitment_requisition_service.service.RequisitionJustificationService;
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
public class RequisitionJustificationServiceImpl implements RequisitionJustificationService {
    private final RequisitionJustificationRepository justificationRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;

    public RequisitionJustificationServiceImpl(RequisitionJustificationRepository justificationRepository,
                                               RecruitmentRequisitionMasterRepository masterRepository) {
        this.justificationRepository = justificationRepository;
        this.masterRepository = masterRepository;
    }

    private RequisitionJustificationDto convertToDto(RequisitionJustification entity) {
        return EntityAndDtoAllMapper.requisitionJustificationEntityToDto(entity);
    }

     private void copyNonNullProperties(RequisitionJustificationParam source, RequisitionJustification target) {
        if (source.getSummary() != null) target.setSummary(source.getSummary());
        if (source.getDetails() != null) target.setDetails(source.getDetails());
    }

    @Override
    public BaseResponse save(RequisitionJustificationParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        RequisitionJustification entity = new RequisitionJustification();
        BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

        entity.setRecruitmentRequisitionMaster(parent);
        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setCreatedBy(param.getActedUserCode());
        entity.setActedUserName(param.getActedUserName());

        RequisitionJustification savedEntity = justificationRepository.save(entity);

        response.setRequisitionJustificationDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse savedAll(List<RequisitionJustificationParam> params) {
        BaseResponse response = new BaseResponse();

        if (params == null || params.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        List<RequisitionJustification> entitiesToSave = params.stream()
                .map(param -> {
                    RecruitmentRequisitionMaster parent = masterRepository
                            .findById(param.getRecruitmentRequisitionMasterId())
                            .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

                    RequisitionJustification entity = new RequisitionJustification();
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

        List<RequisitionJustification> savedEntities = justificationRepository.saveAll(entitiesToSave);
        List<RequisitionJustificationDto> dtoList = savedEntities.stream().map(this::convertToDto).collect(Collectors.toList());

        response.setRequisitionJustificationDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionJustificationParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionJustification existing = justificationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Justification not found for ID: " + id));

        copyNonNullProperties(param, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionJustification updatedEntity = justificationRepository.save(existing);
        response.setRequisitionJustificationDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionJustification existing = justificationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Justification not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        existing.setUpdatedAt(LocalDateTime.now());
        justificationRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionJustification entity = justificationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Justification not found for ID: " + id));

        response.setRequisitionJustificationDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByRequisitionMasterId(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionJustification> entityList = justificationRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE,masterId);

        List<RequisitionJustificationDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionJustificationDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionJustification> entityList = justificationRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionJustificationDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionJustificationDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionJustification> entityPage = justificationRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionJustificationDto> paginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(EntityAndDtoAllMapper::requisitionJustificationEntityToDto));

        response.setRequisitionJustificationDtoPaginatedResponse(paginatedResponse);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
}
