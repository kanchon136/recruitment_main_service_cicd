package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionRequirementsAllocationDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionRequirementsAllocation;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionRequirementsAllocationParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionRequirementsAllocationRepository;
import com.cd.recruitment_requisition_service.service.RequisitionRequirementsAllocationService;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionRequirementsAllocationServiceImpl implements RequisitionRequirementsAllocationService {
    private final RequisitionRequirementsAllocationRepository allocationRepository;
    private final RecruitmentRequisitionMasterRepository requisitionMasterRepository;

    public RequisitionRequirementsAllocationServiceImpl(RequisitionRequirementsAllocationRepository
                                                                allocationRepository, RecruitmentRequisitionMasterRepository requisitionMasterRepository) {
        this.allocationRepository = allocationRepository;
        this.requisitionMasterRepository = requisitionMasterRepository;
    }

    private RequisitionRequirementsAllocationDto convertToDto(RequisitionRequirementsAllocation entity) {
        return EntityAndDtoAllMapper.allocationEntityToDto(entity);
    }


    @Override
    public BaseResponse save(RequisitionRequirementsAllocationParam param) {
        BaseResponse response = new BaseResponse();

        // 1. Validate Parent Existence
        RecruitmentRequisitionMaster parent = requisitionMasterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Recruitment Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));
//
//        // 2. BUSINESS LOGIC: Duplicate Validation (Shift + Personal Sub Area)
//        boolean exists = allocationRepository.existsByRecruitmentRequisitionMasterIdAndShiftAllocationCodeAndPersonalSubAreaCode(
//                param.getRecruitmentRequisitionMasterId(),
//                param.getShiftAllocationCode(),
//                param.getPersonalSubAreaCode()
//        );
//
//        if (exists) {
//            throw new CustomException("Duplicate entry: This Shift and Personal Sub Area combination is already allocated for this requisition.");
//        }

        // 3. Map and Save
        RequisitionRequirementsAllocation allocation = new RequisitionRequirementsAllocation();
        BeanUtils.copyProperties(param, allocation);
        allocation.setRecruitmentRequisitionMaster(parent);

        allocation.setRecordStatus(RecordStatus.ACTIVE);
        allocation.setCreatedDate(LocalDate.now());
        allocation.setCreatedDateTime(LocalDateTime.now());
        allocation.setCreatedBy(param.getActedUserCode());
        allocation.setActedUserName(parent.getActedUserName());

        RequisitionRequirementsAllocation savedEntity = allocationRepository.save(allocation);

        // 4. Prepare Response
        response.setRequisitionRequirementsAllocationDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Transactional
    @Override
    public BaseResponse saveAll(List<RequisitionRequirementsAllocationParam> params) {
        BaseResponse response = new BaseResponse();

        if (params == null || params.isEmpty()) {
            throw new CustomException("The parameter list is empty.");
        }

         Long masterId = params.get(0).getRecruitmentRequisitionMasterId();

         RecruitmentRequisitionMaster parent = requisitionMasterRepository
                .findById(masterId)
                .orElseThrow(() -> new CustomException("Master Requisition not found for ID: " + masterId));

         boolean exists = allocationRepository.existsByRecruitmentRequisitionMasterId(masterId);

        if (exists) {
            allocationRepository.deleteAllByRecruitmentRequisitionMasterId(masterId);
            // Sync the deletion so the new inserts don't conflict with unique constraints
            allocationRepository.flush();
        }

        // 4. Map the list of Params to a list of Entities
        List<RequisitionRequirementsAllocation> entitiesToSave = params.stream().map(param -> {
            RequisitionRequirementsAllocation allocation = new RequisitionRequirementsAllocation();
            BeanUtils.copyProperties(param, allocation);

            allocation.setRecruitmentRequisitionMaster(parent);
            allocation.setRecordStatus(RecordStatus.ACTIVE);
            allocation.setCreatedDate(LocalDate.now());
            allocation.setCreatedDateTime(LocalDateTime.now());
            allocation.setCreatedBy(param.getActedUserCode());

            return allocation;
        }).collect(Collectors.toList());

         List<RequisitionRequirementsAllocation> savedEntities = allocationRepository.saveAll(entitiesToSave);

         List<RequisitionRequirementsAllocationDto> dtos = savedEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionRequirementsAllocationDtos(dtos);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionRequirementsAllocationParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionRequirementsAllocation existing = allocationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requirements Allocation not found for ID: " + id));

        copyNonNullProperties(param, existing);

        //   existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionRequirementsAllocation updatedEntity = allocationRepository.save(existing);

        response.setRequisitionRequirementsAllocationDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionRequirementsAllocation existing = allocationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requirements Allocation not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        //   existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        allocationRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionRequirementsAllocation entity = allocationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requirements Allocation not found for ID: " + id));

        response.setRequisitionRequirementsAllocationDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionRequirementsAllocation> entityList = allocationRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionRequirementsAllocationDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionRequirementsAllocationDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionRequirementsAllocation> entityPage = allocationRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionRequirementsAllocationDto> requisitionRequirementsAllocationDtoPaginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(EntityAndDtoAllMapper::allocationEntityToDto));

        response.setRequisitionRequirementsAllocationDtoPaginatedResponse(requisitionRequirementsAllocationDtoPaginatedResponse);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionRequirementsAllocation> entityList = allocationRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, masterId);

        List<RequisitionRequirementsAllocationDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionRequirementsAllocationDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RequisitionRequirementsAllocationParam source, RequisitionRequirementsAllocation target) {
        if (source.getShiftAllocationCode() != null) target.setShiftAllocationCode(source.getShiftAllocationCode());
        if (source.getShiftAllocationName() != null) target.setShiftAllocationName(source.getShiftAllocationName());
        if (source.getNoOfRequirements() != null) target.setNoOfRequirements(source.getNoOfRequirements());
        if (source.getPersonalSubAreaCode() != null) target.setPersonalSubAreaCode(source.getPersonalSubAreaCode());
        if (source.getPersonalSubAreaName() != null) target.setPersonalSubAreaName(source.getPersonalSubAreaName());
        if (source.getOrgaUnitCode() != null) target.setOrgaUnitCode(source.getOrgaUnitCode());
        if (source.getOrgaUnitName() != null) target.setOrgaUnitName(source.getOrgaUnitName());
        if (source.getApprovedPlanId() != null) target.setApprovedPlanId(source.getApprovedPlanId());
    }

    private static boolean checkNullAndEmptyString(String value) {
        return value == null || value.trim().isEmpty();
    }
}
