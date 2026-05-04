package com.cd.recruitment_requisition_service.mapper;

import com.cd.recruitment_requisition_service.dto.*;
import com.cd.recruitment_requisition_service.entity.*;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.param.RequisitionSupportingDocumentsChildParam;
import com.cd.recruitment_requisition_service.repository.RequisitionReplacementPersonRepository;
import com.cd.recruitment_requisition_service.utils.HostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EntityAndDtoAllMapper {

    public static RecruitmentRequisitionMasterDto requisitionEntityToDto(RecruitmentRequisitionMaster savedEntity) {
        RecruitmentRequisitionMasterDto dto = new RecruitmentRequisitionMasterDto();
        BeanUtils.copyProperties(savedEntity, dto);
        return dto;
    }

    public static RecruitmentRequisitionMasterDto requisitionMasterEntityToDtoWithAllReferenceDto(
            RecruitmentRequisitionMaster savedEntity) {
        RecruitmentRequisitionMasterDto dto = new RecruitmentRequisitionMasterDto();
        BeanUtils.copyProperties(savedEntity, dto);
        if (savedEntity.getJobDescriptions() != null) {
            dto.setJobDescriptions(savedEntity.getJobDescriptions().stream()
                    .map(EntityAndDtoAllMapper::jobDescriptionEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getExpertiseEmployeeCategoryInfos() != null) {
            dto.setAreasOfExpertise(savedEntity.getExpertiseEmployeeCategoryInfos()
                    .stream().map(EntityAndDtoAllMapper::categoryInfoEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getEducations() != null) {
            dto.setEducations(savedEntity.getEducations()
                    .stream().map(EntityAndDtoAllMapper::educationEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getAllocations() != null) {
            dto.setAllocations(savedEntity.getAllocations()
                    .stream().map(EntityAndDtoAllMapper::allocationEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getApprovalChannels() != null) {
            dto.setApprovalChannels(savedEntity.getApprovalChannels().stream()
                    .map(EntityAndDtoAllMapper::requisitionApprovedChannelEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getDocuments() != null) {
            dto.setDocuments(savedEntity.getDocuments().stream().map(EntityAndDtoAllMapper::masterEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionIndustries() != null && !savedEntity.getRequisitionIndustries().isEmpty()) {
            dto.setRequisitionIndustries(savedEntity.getRequisitionIndustries().stream()
                    .map(EntityAndDtoAllMapper::requisitionIndustryEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionAdditionalRequirements() != null) {
            dto.setRequisitionAdditionalRequirements(savedEntity.getRequisitionAdditionalRequirements().stream()
                    .map(EntityAndDtoAllMapper::requisitionAdditionalRequirementsEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionAgeAndNationalities() != null) {
            dto.setRequisitionAgeAndNationalities(savedEntity.getRequisitionAgeAndNationalities().stream()
                    .map(EntityAndDtoAllMapper::requisitionAgeAndNationalityEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionJustifications() != null) {
            dto.setRequisitionJustifications(savedEntity.getRequisitionJustifications()
                    .stream().map(EntityAndDtoAllMapper::requisitionJustificationEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getReplacementPersons() != null) {
            dto.setRequisitionReplacementPersonDtos(savedEntity.getReplacementPersons().stream()
                    .map(EntityAndDtoAllMapper::requisitionReplacementPersonEntityToDto).collect(Collectors.toList()));
        }

        return dto;
    }


    public static RecruitmentRequisitionMasterDto requisitionMasterEntityToDtoWithAllReferenceDto(
            RecruitmentRequisitionMaster savedEntity, RequisitionReplacementPersonRepository personRepository) {
        RecruitmentRequisitionMasterDto dto = new RecruitmentRequisitionMasterDto();
        BeanUtils.copyProperties(savedEntity, dto);
        if (savedEntity.getJobDescriptions() != null) {
            dto.setJobDescriptions(savedEntity.getJobDescriptions().stream()
                    .map(EntityAndDtoAllMapper::jobDescriptionEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getExpertiseEmployeeCategoryInfos() != null) {
            dto.setAreasOfExpertise(savedEntity.getExpertiseEmployeeCategoryInfos()
                    .stream().map(EntityAndDtoAllMapper::categoryInfoEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getEducations() != null) {
            dto.setEducations(savedEntity.getEducations()
                    .stream().map(EntityAndDtoAllMapper::educationEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getAllocations() != null) {
            dto.setAllocations(savedEntity.getAllocations()
                    .stream().map(EntityAndDtoAllMapper::allocationEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getApprovalChannels() != null) {
            dto.setApprovalChannels(savedEntity.getApprovalChannels().stream()
                    .map(EntityAndDtoAllMapper::requisitionApprovedChannelEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getDocuments() != null) {
            dto.setDocuments(savedEntity.getDocuments().stream().map(EntityAndDtoAllMapper::masterEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionIndustries() != null && !savedEntity.getRequisitionIndustries().isEmpty()) {
            dto.setRequisitionIndustries(savedEntity.getRequisitionIndustries().stream()
                    .map(EntityAndDtoAllMapper::requisitionIndustryEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionAdditionalRequirements() != null) {
            dto.setRequisitionAdditionalRequirements(savedEntity.getRequisitionAdditionalRequirements().stream()
                    .map(EntityAndDtoAllMapper::requisitionAdditionalRequirementsEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionAgeAndNationalities() != null) {
            dto.setRequisitionAgeAndNationalities(savedEntity.getRequisitionAgeAndNationalities().stream()
                    .map(EntityAndDtoAllMapper::requisitionAgeAndNationalityEntityToDto).collect(Collectors.toList()));
        }

        if (savedEntity.getRequisitionJustifications() != null) {
            dto.setRequisitionJustifications(savedEntity.getRequisitionJustifications()
                    .stream().map(EntityAndDtoAllMapper::requisitionJustificationEntityToDto)
                    .collect(Collectors.toList()));
        }

        if (savedEntity.getReplacementPersons() != null) {
            dto.setRequisitionReplacementPersonDtos(savedEntity.getReplacementPersons().stream()
                    .map(person->EntityAndDtoAllMapper.requisitionReplacementPersonEntityToDto(person,personRepository)).collect(Collectors.toList()));
        }

        return dto;
    }


    public static RequisitionJobTaskDto jobTaskEntityToDto(RequisitionJobTask requisitionJobTask) {
        RequisitionJobTaskDto dto = new RequisitionJobTaskDto();
        BeanUtils.copyProperties(requisitionJobTask, dto);
        return dto;
    }

    public static RequisitionJobDescriptionDto jobDescriptionEntityToDto(RequisitionJobDescription entity) {
        if (entity == null)
            return null;

        RequisitionJobDescriptionDto dto = new RequisitionJobDescriptionDto();
        BeanUtils.copyProperties(entity, dto);

        // Recruitment Requisition Master ID
        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }

        // Level 2 (JobList) ম
        if (entity.getRequisitionJobLists() != null) {
            List<RequisitionJobListDto> jobListDtos = entity.getRequisitionJobLists().stream()
                    .filter(f -> f.getRecordStatus() == RecordStatus.ACTIVE)
                    .map(EntityAndDtoAllMapper::jobListEntityToDto) //
                    .collect(Collectors.toList());
            dto.setRequisitionJobListDtos(jobListDtos);
        }

        return dto;
    }

    public static RequisitionJobListDto jobListEntityToDto(RequisitionJobList entity) {
        if (entity == null)
            return null;

        RequisitionJobListDto dto = new RequisitionJobListDto();
        BeanUtils.copyProperties(entity, dto);

        // Level 3 (Tasks)
        if (entity.getTasks() != null) {
            List<RequisitionJobTaskDto> taskDtos = entity.getTasks().stream()
                    .filter(f -> f.getRecordStatus() == RecordStatus.ACTIVE)
                    .map(EntityAndDtoAllMapper::jobTaskEntityToDto)
                    .collect(Collectors.toList());
            dto.setRequisitionJobTaskDtos(taskDtos);
        }

        return dto;
    }

    public static RequisitionEducationDto educationEntityToDto(RequisitionEducation entity) {
        RequisitionEducationDto dto = new RequisitionEducationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionAreaOfExpertiseDto expertiseEntityToDto(RequisitionAreaOfExpertise entity) {
        RequisitionAreaOfExpertiseDto dto = new RequisitionAreaOfExpertiseDto();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getSkills() != null) {
            List<RequisitionAreaOfExpertiseSkillDto> requisitionAreaOfExpertiseSkillDtos = entity.getSkills().stream()
                    .map(EntityAndDtoAllMapper::skillEntityToDto).collect(Collectors.toList());
            dto.setRequisitionAreaOfExpertiseSkillDtos(requisitionAreaOfExpertiseSkillDtos);
        }

        return dto;
    }

    public static RequisitionAreaOfExpertiseSkillDto skillEntityToDto(RequisitionAreaOfExpertiseSkill entity) {
        RequisitionAreaOfExpertiseSkillDto dto = new RequisitionAreaOfExpertiseSkillDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionSupportingDocumentsMasterDto masterEntityToDto(
            RequisitionSupportingDocumentsMaster entity) {
        RequisitionSupportingDocumentsMasterDto dto = new RequisitionSupportingDocumentsMasterDto();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }
        if (entity.getFiles() != null) {
            List<RequisitionSupportingDocumentsChildDto> requisitionSupportingDocumentsChildDtos = entity.getFiles()
                    .stream().map(EntityAndDtoAllMapper::requisitionSupportingDocumentsChildEntityToDto)
                    .collect(Collectors.toList());
            dto.setFiles(requisitionSupportingDocumentsChildDtos);

        }
        return dto;

    }

    public static RequisitionSupportingDocumentsChildDto requisitionSupportingDocumentsChildEntityToDto(
            RequisitionSupportingDocumentsChild entity) {
        RequisitionSupportingDocumentsChildDto dto = new RequisitionSupportingDocumentsChildDto();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getRequisitionSupportingDocumentsMaster() != null) {
            dto.setRequisitionSupportingDocumentsMasterId(entity.getRequisitionSupportingDocumentsMaster().getId());
        }

        if (entity.getFileName() != null) {
            String encodedFileName = UriUtils.encodePathSegment(entity.getFileName(), StandardCharsets.UTF_8);
            String fullUrl = HostUtils.getHostUrl()
                    + "/api/v1/requisition/requisitionSupportingDocumentsMaster/loadFileAsResource/"
                    + entity.getRequisitionSupportingDocumentsMaster().getId() + "/" + encodedFileName;
            log.info("fullUrl==========================>"+fullUrl);
            dto.setFilePath(fullUrl);
        }
        return dto;
    }

    public static RequisitionSupportingDocumentsChild childParamToEntity(
            RequisitionSupportingDocumentsChildParam param) {
        RequisitionSupportingDocumentsChild entity = new RequisitionSupportingDocumentsChild();
        BeanUtils.copyProperties(param, entity);
        return entity;
    }

    public static RequisitionRequirementsAllocationDto allocationEntityToDto(RequisitionRequirementsAllocation entity) {
        RequisitionRequirementsAllocationDto dto = new RequisitionRequirementsAllocationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionIndustryDto requisitionIndustryEntityToDto(RequisitionIndustry entity) {
        RequisitionIndustryDto dto = new RequisitionIndustryDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionApprovedChannelDto requisitionApprovedChannelEntityToDto(
            RequisitionApprovedChannel entity) {
        RequisitionApprovedChannelDto dto = new RequisitionApprovedChannelDto();

        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    public static RequisitionAgeAndNationalityDto requisitionAgeAndNationalityEntityToDto(
            RequisitionAgeAndNationality entity) {

        RequisitionAgeAndNationalityDto dto = new RequisitionAgeAndNationalityDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionAdditionalRequirementsDto requisitionAdditionalRequirementsEntityToDto(
            RequisitionAdditionalRequirements entity) {

        RequisitionAdditionalRequirementsDto dto = new RequisitionAdditionalRequirementsDto();
        BeanUtils.copyProperties(entity, dto);

        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }
        return dto;
    }

    public static RequisitionJustificationDto requisitionJustificationEntityToDto(RequisitionJustification entity) {
        RequisitionJustificationDto dto = new RequisitionJustificationDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static RequisitionReplacementPersonDto requisitionReplacementPersonEntityToDto(
            RequisitionReplacementPerson entity) {
        if (entity == null) {
            return null;
        }

        RequisitionReplacementPersonDto dto = new RequisitionReplacementPersonDto();

        BeanUtils.copyProperties(entity, dto);

        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }

        return dto;
    }
    // মেথডে repository পাস করা হচ্ছে
    public static RequisitionReplacementPersonDto requisitionReplacementPersonEntityToDto(
            RequisitionReplacementPerson entity,
            RequisitionReplacementPersonRepository repository) {

        if (entity == null) return null;

        RequisitionReplacementPersonDto dto = new RequisitionReplacementPersonDto();
        BeanUtils.copyProperties(entity, dto);

        if (entity.getRecruitmentRequisitionMaster() != null) {
            dto.setRecruitmentRequisitionMasterId(entity.getRecruitmentRequisitionMaster().getId());
        }
//        // রান-টাইম ক্যালকুলেশন: শুধুমাত্র যদি রিপোজিটরি এবং এমপ্লয়ি আইডি থাকে
//        if (repository != null && entity.getEmployeeId() != null) {
//            long approvedCount = repository
//                    .countByEmployeeIdAndRecordStatusAndRequisitionIdIsNotNullAndRecruitmentRequisitionMaster_CurrentStatus(
//                            entity.getEmployeeId(),
//                            RecordStatus.ACTIVE,
//                            com.cd.recruitment_requisition_service.enums.RequisitionStatus.FINAL_APPROVED
//                    );
//            dto.setApprovalFrequency((int) approvedCount);
//        }

        return dto;
    }

    public static ExpertiseEmployeeCategoryInfoDto categoryInfoEntityToDto(ExpertiseEmployeeCategoryInfo saved) {
        ExpertiseEmployeeCategoryInfoDto dto = new ExpertiseEmployeeCategoryInfoDto();
        BeanUtils.copyProperties(saved, dto);

        if (saved.getAreaOfExpertises() != null) {
            dto.setRequisitionAreaOfExpertiseDtos(
                    saved.getAreaOfExpertises().stream().map(EntityAndDtoAllMapper::expertiseEntityToDto).toList());
        }
        return dto;
    }

}
