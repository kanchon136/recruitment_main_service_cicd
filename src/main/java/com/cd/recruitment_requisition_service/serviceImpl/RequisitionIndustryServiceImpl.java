package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionIndustryDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionIndustry;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionIndustryParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionIndustryRepository;
import com.cd.recruitment_requisition_service.service.RequisitionIndustryService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionIndustryServiceImpl implements RequisitionIndustryService {
    private final RequisitionIndustryRepository industryRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;

    public RequisitionIndustryServiceImpl(RequisitionIndustryRepository industryRepository,
                                          RecruitmentRequisitionMasterRepository masterRepository) {
        this.industryRepository = industryRepository;
        this.masterRepository = masterRepository;
    }

    private RequisitionIndustryDto convertToDto(RequisitionIndustry entity) {
        return EntityAndDtoAllMapper.requisitionIndustryEntityToDto(entity);
    }


    @Override
    @Transactional
    public BaseResponse save(RequisitionIndustryParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        RequisitionIndustry requisitionIndustry = new RequisitionIndustry();

        BeanUtils.copyProperties(param, requisitionIndustry, "id", "recruitmentRequisitionMasterId");

        requisitionIndustry.setRecruitmentRequisitionMaster(parent);

        requisitionIndustry.setRecordStatus(RecordStatus.ACTIVE);
         requisitionIndustry.setCreatedDate(LocalDate.now());
         requisitionIndustry.setCreatedDateTime(LocalDateTime.now());
         requisitionIndustry.setCreatedBy(param.getActedUserCode());
         requisitionIndustry.setActedUserName(param.getActedUserName());

        RequisitionIndustry savedEntity = industryRepository.save(requisitionIndustry);

        response.setRequisitionIndustryDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    @Transactional
    public BaseResponse saveAll(List<RequisitionIndustryParam> requisitionIndustryParams) {
        BaseResponse response = new BaseResponse();

        if (requisitionIndustryParams == null || requisitionIndustryParams.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        List<RequisitionIndustry> entitiesToSave = requisitionIndustryParams.stream()
                .map(param -> {

                    RecruitmentRequisitionMaster parent = masterRepository
                            .findById(param.getRecruitmentRequisitionMasterId())
                            .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

                    RequisitionIndustry requisitionIndustry = new RequisitionIndustry();

                    BeanUtils.copyProperties(param, requisitionIndustry, "id", "recruitmentRequisitionMasterId");

                    requisitionIndustry.setRecruitmentRequisitionMaster(parent);

                    requisitionIndustry.setRecordStatus(RecordStatus.ACTIVE);
                     requisitionIndustry.setCreatedDate(LocalDate.now());
                     requisitionIndustry.setCreatedDateTime(LocalDateTime.now());
                     requisitionIndustry.setCreatedBy(param.getActedUserCode());
                     requisitionIndustry.setActedUserName(parent.getActedUserName());
                    return requisitionIndustry;
                })
                .collect(Collectors.toList());

        List<RequisitionIndustry> savedEntities = industryRepository.saveAll(entitiesToSave);

        List<RequisitionIndustryDto> dtoList = savedEntities.stream().map(this::convertToDto).collect(Collectors.toList());
        response.setRequisitionIndustryDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionIndustryParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionIndustry existing = industryRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Industry not found for ID: " + id));

        copyNonNullProperties(param, existing);

         existing.setUpdatedBy(param.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());

        RequisitionIndustry updatedEntity = industryRepository.save(existing);

        response.setRequisitionIndustryDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionIndustry existing = industryRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Industry not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        industryRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionIndustry entity = industryRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Industry not found for ID: " + id));

        response.setRequisitionIndustryDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByParentId(Long parentId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionIndustry> entityList = industryRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, parentId);

        List<RequisitionIndustryDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionIndustryDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionIndustry> entityList = industryRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionIndustryDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionIndustryDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
//        PaginatedResponse response = new PaginatedResponse();
//        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
//
//        // Assuming a method to get active records with pagination is needed, though JpaRepository.findAll(Pageable) returns all.
//        // For simplicity, using findAll for pagination here, which may include deleted records if not filtered in the DTO mapper.
//        Page<RequisitionIndustry> entityPage = industryRepository.findAll(pageRequest);
//
//        // Filtered page content logic would be needed here if findAll() returned all records status
//        List<RequisitionIndustryDto> dtoList = entityPage.getContent().stream()
//                .filter(e -> e.getRecordStatus() == RecordStatus.ACTIVE) // Manual filtering if repo.findAll() doesn't filter by default
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//
//        // response.setData(dtoList);
//        response.setPageNo(entityPage.getNumber());
//        response.setPageSize(entityPage.getSize());
//        response.setTotalElements(entityPage.getTotalElements());
//        response.setTotalPages(entityPage.getTotalPages());
//        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return null;
    }

    private void copyNonNullProperties(RequisitionIndustryParam source, RequisitionIndustry target) {
        if (source.getIndustryCode() != null) target.setIndustryCode(source.getIndustryCode());
        if (source.getIndustryName() != null) target.setIndustryName(source.getIndustryName());
    }
}
