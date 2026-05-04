package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionModuleCountDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.repository.*;
import com.cd.recruitment_requisition_service.service.RequisitionModuleCountService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RequisitionModuleCountServiceImpl implements RequisitionModuleCountService {

    private final RecruitmentRequisitionMasterRepository recruitmentRequisitionMasterRepository;
    private final RequisitionAreaOfExpertiseRepository requisitionAreaOfExpertiseRepository;
    private final RequisitionEducationRepository requisitionEducationRepository;
    private final RequisitionRequirementsAllocationRepository requisitionRequirementsAllocationRepository;
    private final RequisitionSupportingDocumentsMasterRepository requisitionSupportingDocumentsMasterRepository;
    private final RequisitionJobDescriptionRepository requisitionJobDescriptionRepository;

     private final RequisitionJustificationRepository requisitionJustificationRepository;
    private final RequisitionIndustryRepository requisitionIndustryRepository;
    private final RequisitionApprovedChannelRepository requisitionApprovedChannelRepository;
    private final RequisitionAgeAndNationalityRepository requisitionAgeAndNationalityRepository;
    private final RequisitionAdditionalRequirementsRepository requisitionAdditionalRequirementsRepository;
    private final RequisitionReplacementPersonRepository replacementPersonRepository;
    private final ExpertiseEmployeeCategoryInfoRepository expertiseEmployeeCategoryInfoRepository;



    public RequisitionModuleCountServiceImpl(
            RecruitmentRequisitionMasterRepository recruitmentRequisitionMasterRepository,
            RequisitionAreaOfExpertiseRepository requisitionAreaOfExpertiseRepository,
            RequisitionEducationRepository requisitionEducationRepository,
            RequisitionRequirementsAllocationRepository requisitionRequirementsAllocationRepository,
            RequisitionSupportingDocumentsMasterRepository requisitionSupportingDocumentsMasterRepository,
            RequisitionJobDescriptionRepository requisitionJobDescriptionRepository,
            RequisitionJustificationRepository requisitionJustificationRepository,
            RequisitionIndustryRepository requisitionIndustryRepository,
            RequisitionApprovedChannelRepository requisitionApprovedChannelRepository,
            RequisitionAgeAndNationalityRepository requisitionAgeAndNationalityRepository,
            RequisitionAdditionalRequirementsRepository requisitionAdditionalRequirementsRepository,
            RequisitionReplacementPersonRepository replacementPersonRepository,
            ExpertiseEmployeeCategoryInfoRepository  expertiseEmployeeCategoryInfoRepository) {

        this.recruitmentRequisitionMasterRepository = recruitmentRequisitionMasterRepository;
        this.requisitionAreaOfExpertiseRepository = requisitionAreaOfExpertiseRepository;
        this.requisitionEducationRepository = requisitionEducationRepository;
        this.requisitionRequirementsAllocationRepository = requisitionRequirementsAllocationRepository;
        this.requisitionSupportingDocumentsMasterRepository = requisitionSupportingDocumentsMasterRepository;
        this.requisitionJobDescriptionRepository = requisitionJobDescriptionRepository;

        this.requisitionJustificationRepository = requisitionJustificationRepository;
        this.requisitionIndustryRepository = requisitionIndustryRepository;
        this.requisitionApprovedChannelRepository = requisitionApprovedChannelRepository;
        this.requisitionAgeAndNationalityRepository = requisitionAgeAndNationalityRepository;
        this.requisitionAdditionalRequirementsRepository = requisitionAdditionalRequirementsRepository;
        this.replacementPersonRepository = replacementPersonRepository;
        this.expertiseEmployeeCategoryInfoRepository = expertiseEmployeeCategoryInfoRepository;
    }

    @Override
    public BaseResponse findALlRequisitionModuleCountData(Long parenTRequisitionId) {
        BaseResponse baseResponse = new BaseResponse();
        RequisitionModuleCountDto requisitionModuleCountDto = new RequisitionModuleCountDto();
        RecordStatus active = RecordStatus.ACTIVE;

        // 1. Validate Parent Requisition Master
        RecruitmentRequisitionMaster recruitmentRequisitionMaster = recruitmentRequisitionMasterRepository
                .findByIdAndRecordStatus(parenTRequisitionId, active).orElseThrow(()->
                        new CustomException("Parent Data not fount by this parentId: "+parenTRequisitionId));


        requisitionModuleCountDto.setRequisitionMasterId(recruitmentRequisitionMaster.getId());
        requisitionModuleCountDto.setRequisitionCode(recruitmentRequisitionMaster.getRequisitionCode());



        // 2. Education Module Check
        requisitionModuleCountDto.setEducation(requisitionEducationRepository.
                findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()? 0:1);

        // 3. AreaOfExpertise Module Check
        requisitionModuleCountDto.setAreaOfExpertise(expertiseEmployeeCategoryInfoRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 4. Requirements Allocation Module Check
        requisitionModuleCountDto.setRequirementsAllocation(requisitionRequirementsAllocationRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 5. Supporting Documents Module Check
        // Note: DTO field name assumed to be 'supportingDocuments'
        requisitionModuleCountDto.setSupportingDocuments(requisitionSupportingDocumentsMasterRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 6. Job Description Module Check
        requisitionModuleCountDto.setJobDescription(requisitionJobDescriptionRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 7. Justification Module Check
        requisitionModuleCountDto.setJustification(requisitionJustificationRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 8. Industry Module Check
        requisitionModuleCountDto.setIndustry(requisitionIndustryRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 9. Approved Channel Module Check
        requisitionModuleCountDto.setApprovedChannel(requisitionApprovedChannelRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 10. Age And Nationality Module Check (Maps to nationality in DTO)
        requisitionModuleCountDto.setNationality(requisitionAgeAndNationalityRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);

        // 11. Additional Requirements Module Check
        requisitionModuleCountDto.setAdditionalRequirement(requisitionAdditionalRequirementsRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                        active,parenTRequisitionId).isEmpty()?0:1);


        requisitionModuleCountDto.setRequisitionReplacementPerson(replacementPersonRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(active,parenTRequisitionId).isEmpty()? 0:1);


        // 12. Final Response Setup
        baseResponse.setRequisitionModuleCountDto(requisitionModuleCountDto);
        baseResponse.setMessage(ResponseEnum.SUCCESS.getStatus());

        return baseResponse;
    }
}
