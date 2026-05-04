package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionAreaOfExpertiseSkillDto;
import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertise;
import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertiseSkill;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseSkillParam;
import com.cd.recruitment_requisition_service.repository.RequisitionAreaOfExpertiseRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionAreaOfExpertiseSkillRepository;
import com.cd.recruitment_requisition_service.service.RequisitionAreaOfExpertiseSkillService;
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
public class RequisitionAreaOfExpertiseSkillServiceImpl  implements RequisitionAreaOfExpertiseSkillService {

    private final RequisitionAreaOfExpertiseSkillRepository skillRepository;
    private final RequisitionAreaOfExpertiseRepository expertiseRepository; // Parent Repository

    public RequisitionAreaOfExpertiseSkillServiceImpl(RequisitionAreaOfExpertiseSkillRepository skillRepository,
                                                      RequisitionAreaOfExpertiseRepository expertiseRepository) {
        this.skillRepository = skillRepository;
        this.expertiseRepository = expertiseRepository;
    }


    @Override
    @Transactional
     public BaseResponse findAllByRecordStatusAndRequisitionAreaOfExpertise_idAndRecordStatus(Long parentId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionAreaOfExpertiseSkill> list = skillRepository
                .findAllByRecordStatusAndRequisitionAreaOfExpertise_idAndRecordStatus(RecordStatus.ACTIVE, parentId,RecordStatus.ACTIVE);

        List<RequisitionAreaOfExpertiseSkillDto> dtoList = list.stream()
                .map(EntityAndDtoAllMapper::skillEntityToDto)
                .collect(Collectors.toList());

         response.setRequisitionAreaOfExpertiseSkillDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse save(RequisitionAreaOfExpertiseSkillParam param) {
        BaseResponse response = new BaseResponse();
         RequisitionAreaOfExpertiseSkill entity = new RequisitionAreaOfExpertiseSkill();
        BeanUtils.copyProperties(param, entity);

         RequisitionAreaOfExpertise parent = expertiseRepository
                .findByIdAndRecordStatus(param.getRequisitionAreaOfExpertiseId(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Active Requisition Area of Expertise not found for ID: " + param.getRequisitionAreaOfExpertiseId()));

        entity.setRequisitionAreaOfExpertise(parent);

         entity.setRecordStatus(RecordStatus.ACTIVE);
         entity.setCreatedBy(param.getActedUserCode());
         entity.setActedUserName(param.getActedUserName());
         entity.setCreatedDate(LocalDate.now());
         entity.setCreatedDateTime(LocalDateTime.now());

         RequisitionAreaOfExpertiseSkill savedEntity = skillRepository.save(entity);

         RequisitionAreaOfExpertiseSkillDto dto = EntityAndDtoAllMapper.skillEntityToDto(savedEntity);
         response.setRequisitionAreaOfExpertiseSkillDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionAreaOfExpertiseSkillParam param) {
        BaseResponse response = new BaseResponse();
        Optional<RequisitionAreaOfExpertiseSkill> optional = skillRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Area of Expertise Skill detail not found for ID: " + id);
            return response;
        }

        RequisitionAreaOfExpertiseSkill existing = optional.get();
        copyNonNullProperties(param, existing);
        existing.setUpdatedBy(param.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());

         if (param.getRequisitionAreaOfExpertiseId() != null) {
            expertiseRepository.findByIdAndRecordStatus(param.getRequisitionAreaOfExpertiseId(), RecordStatus.ACTIVE)
                    .ifPresent(existing::setRequisitionAreaOfExpertise);
        }

        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());

        RequisitionAreaOfExpertiseSkill updated = skillRepository.save(existing);

        RequisitionAreaOfExpertiseSkillDto dto = EntityAndDtoAllMapper.skillEntityToDto(updated);
         response.setRequisitionAreaOfExpertiseSkillDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionAreaOfExpertiseSkill> optional = skillRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Area of Expertise Skill detail not found for ID: " + id);
            return response;
        }

        RequisitionAreaOfExpertiseSkill existing = optional.get();
         existing.setRecordStatus(RecordStatus.DELETED);
       // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        skillRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionAreaOfExpertiseSkill> optional = skillRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Area of Expertise Skill detail not found for ID: " + id);
            return response;
        }

        RequisitionAreaOfExpertiseSkillDto dto = EntityAndDtoAllMapper.skillEntityToDto(optional.get());
         response.setRequisitionAreaOfExpertiseSkillDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdDateTime").descending());
        Page<RequisitionAreaOfExpertiseSkill> page = skillRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        PaginatedResponse<RequisitionAreaOfExpertiseSkillDto> pagination =
                PaginatedResponse.fromPage(page.map(EntityAndDtoAllMapper::skillEntityToDto));

        BaseResponse response = new BaseResponse();
        response.setRequisitionAreaOfExpertiseSkillDtoPaginatedResponse(pagination); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse findAll() {
        var list = skillRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
        BaseResponse response = new BaseResponse();
         response.setRequisitionAreaOfExpertiseSkillDtos(list.stream() // Placeholder DTO setter
                 .map(EntityAndDtoAllMapper::skillEntityToDto)
                 .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RequisitionAreaOfExpertiseSkillParam source, RequisitionAreaOfExpertiseSkill target) {
        if (source.getSkillName() != null) target.setSkillName(source.getSkillName());
    }
}
