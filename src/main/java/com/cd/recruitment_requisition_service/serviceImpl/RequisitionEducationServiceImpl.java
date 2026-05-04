package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionEducationDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionEducation;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionEducationParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionEducationRepository;
import com.cd.recruitment_requisition_service.service.RequisitionEducationService;
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
public class RequisitionEducationServiceImpl implements RequisitionEducationService {
    private final RequisitionEducationRepository educationRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository; // Parent Repository

    public RequisitionEducationServiceImpl(RequisitionEducationRepository educationRepository,
                                           RecruitmentRequisitionMasterRepository masterRepository) {
        this.educationRepository = educationRepository;
        this.masterRepository = masterRepository;
    }

    @Override
    @Transactional
    public BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionEducation> list = educationRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, parentId);

        List<RequisitionEducationDto> dtoList = list.stream()
                .map(EntityAndDtoAllMapper::educationEntityToDto)
                .collect(Collectors.toList());

        response.setRequisitionEducationDtos(dtoList); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse save(RequisitionEducationParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionEducation entity = new RequisitionEducation();
        BeanUtils.copyProperties(param, entity);

        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId()) // Assuming parent ID is in param
                .orElseThrow(() -> new CustomException("Active Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        entity.setRecruitmentRequisitionMaster(parent);

        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedBy(param.getActedUserCode());
        entity.setActedUserName(parent.getActedUserName());
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        RequisitionEducation savedEntity = educationRepository.save(entity);

        RequisitionEducationDto dto = EntityAndDtoAllMapper.educationEntityToDto(savedEntity);
        response.setRequisitionEducationDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionEducationParam param) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionEducation> optional = educationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Education detail not found for ID: " + id);
            return response;
        }

        RequisitionEducation existing = optional.get();
        copyNonNullProperties(param, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        if (param.getRecruitmentRequisitionMasterId() != null) {
            masterRepository.findById(param.getRecruitmentRequisitionMasterId())
                    .ifPresent(existing::setRecruitmentRequisitionMaster);
        }

        //  existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());

        RequisitionEducation updated = educationRepository.save(existing);

        RequisitionEducationDto dto = EntityAndDtoAllMapper.educationEntityToDto(updated);
        response.setRequisitionEducationDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionEducation> optional = educationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Education detail not found for ID: " + id);
            return response;
        }

        RequisitionEducation existing = optional.get();
        existing.setRecordStatus(RecordStatus.DELETED);
        //  existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        educationRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionEducation> optional = educationRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Education detail not found for ID: " + id);
            return response;
        }

        RequisitionEducationDto dto = EntityAndDtoAllMapper.educationEntityToDto(optional.get());
        response.setRequisitionEducationDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdDateTime").descending());
        Page<RequisitionEducation> page = educationRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionEducationDto> pagination =
                PaginatedResponse.fromPage(page.map(EntityAndDtoAllMapper::educationEntityToDto));

        BaseResponse response = new BaseResponse();
        response.setRequisitionEducationDtoPaginatedResponse(pagination); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAll() {
        var list = educationRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
        BaseResponse response = new BaseResponse();
        response.setRequisitionEducationDtos(list.stream() // Placeholder DTO setter
                .map(EntityAndDtoAllMapper::educationEntityToDto)
                .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RequisitionEducationParam source, RequisitionEducation target) {
        // New Fields null checks
        if (source.getLevelOfEducationCode() != null) target.setLevelOfEducationCode(source.getLevelOfEducationCode());
        if (source.getLevelOfEducationName() != null) target.setLevelOfEducationName(source.getLevelOfEducationName());

        if (source.getEducationDegreeCode() != null) target.setEducationDegreeCode(source.getEducationDegreeCode());
        if (source.getEducationDegreeName() != null) target.setEducationDegreeName(source.getEducationDegreeName());

        if (source.getBranchOfStudyCode() != null) target.setBranchOfStudyCode(source.getBranchOfStudyCode());
        if (source.getBranchOfStudyName() != null) target.setBranchOfStudyName(source.getBranchOfStudyName());

        if (source.getActedUserCode() != null) target.setCreatedBy(source.getActedUserCode());
        if (source.getActedUserName() != null) target.setActedUserName(source.getActedUserName());

        if (source.getUpdatedBy() != null) target.setUpdatedBy(source.getUpdatedBy());
    }

}
