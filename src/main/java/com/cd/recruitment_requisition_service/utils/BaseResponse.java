package com.cd.recruitment_requisition_service.utils;


import com.cd.recruitment_requisition_service.dto.*;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertiseSkill;
import com.cd.recruitment_requisition_service.entity.RequisitionEducation;
import com.cd.recruitment_requisition_service.entity.RequisitionSupportingDocumentsMaster;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {

    private String message;

    private String errorData;

    private ResponseEnum responseEnum;

    private RecruitmentRequisitionMasterDto recruitmentRequisitionMasterDto;
    private List<RecruitmentRequisitionMasterDto> recruitmentRequisitionMasterDtos;
    private PaginatedResponse<RecruitmentRequisitionMasterDto> recruitmentRequisitionMasterDtoPaginatedResponse;

    private RequisitionJobTaskDto requisitionJobTaskDto;
    private List<RequisitionJobTaskDto> requisitionJobTaskDtos;
    private PaginatedResponse<RequisitionJobTaskDto> requisitionJobTaskDtoPaginatedResponse;

    private RequisitionJobDescriptionDto requisitionJobDescriptionDto;
    private List<RequisitionJobDescriptionDto> requisitionJobDescriptionDtos;
    private PaginatedResponse<RequisitionJobDescriptionDto> requisitionJobDescriptionDtoPaginatedResponse;

    private RequisitionEducationDto requisitionEducationDto;
    private List<RequisitionEducationDto> requisitionEducationDtos;
    private PaginatedResponse<RequisitionEducationDto> requisitionEducationDtoPaginatedResponse;

    private RequisitionAreaOfExpertiseDto requisitionAreaOfExpertiseDto;
    private List<RequisitionAreaOfExpertiseDto> requisitionAreaOfExpertiseDtos;
    private PaginatedResponse<RequisitionAreaOfExpertiseDto>  requisitionAreaOfExpertiseDtoPaginatedResponse;


    private RequisitionAreaOfExpertiseSkillDto requisitionAreaOfExpertiseSkillDto ;
    private List<RequisitionAreaOfExpertiseSkillDto> requisitionAreaOfExpertiseSkillDtos;
    private PaginatedResponse<RequisitionAreaOfExpertiseSkillDto> requisitionAreaOfExpertiseSkillDtoPaginatedResponse;

    private RequisitionRequirementsAllocationDto requisitionRequirementsAllocationDto;
    private List<RequisitionRequirementsAllocationDto> requisitionRequirementsAllocationDtos;
    private PaginatedResponse<RequisitionRequirementsAllocationDto> requisitionRequirementsAllocationDtoPaginatedResponse;

    private RequisitionModuleCountDto requisitionModuleCountDto;

    private RequisitionSupportingDocumentsMasterDto requisitionSupportingDocumentsMasterDto;

    private List<RequisitionSupportingDocumentsMasterDto> requisitionSupportingDocumentsMasterDtos;
    private PaginatedResponse<RequisitionSupportingDocumentsMasterDto> requisitionSupportingDocumentsMasterDtoPaginatedResponse;


    private RequisitionIndustryDto requisitionIndustryDto;
    private List<RequisitionIndustryDto> requisitionIndustryDtos;
    private PaginatedResponse<RequisitionIndustryDto> requisitionIndustryDtoPaginatedResponse;

    private RequisitionApprovedChannelDto requisitionApprovedChannelDto;
    private List<RequisitionApprovedChannelDto> requisitionApprovedChannelDtos;
   private PaginatedResponse<RequisitionApprovedChannelDto> requisitionApprovedChannelDtoPaginatedResponse;

   private RequisitionAgeAndNationalityDto requisitionAgeAndNationalityDto;
   private List<RequisitionAgeAndNationalityDto> requisitionAgeAndNationalityDtos;
   private PaginatedResponse<RequisitionAgeAndNationalityDto> requisitionAgeAndNationalityDtoPaginatedResponse;

  private RequisitionAdditionalRequirementsDto requisitionAdditionalRequirementsDto;
  private List<RequisitionAdditionalRequirementsDto> requisitionAdditionalRequirementsDtos;
  private PaginatedResponse<RequisitionAdditionalRequirementsDto> requisitionAdditionalRequirementsDtoPaginatedResponse;


  private  RequisitionJustificationDto requisitionJustificationDto;
  private List<RequisitionJustificationDto> requisitionJustificationDtos;
  private PaginatedResponse<RequisitionJustificationDto> requisitionJustificationDtoPaginatedResponse;

  private RequisitionStatusHistoryDto requisitionStatusHistoryDto;
  private List<RequisitionStatusHistoryDto> requisitionStatusHistoryDtos;
  private PaginatedResponse<RequisitionStatusHistoryDto> requisitionStatusHistoryDtoPaginatedResponse;

  private RequisitionActionDto requisitionActionDto;
  private List<RequisitionActionDto> requisitionActionDtos;
  private PaginatedResponse<RequisitionActionDto> requisitionActionDtoPaginatedResponse;

  private RequisitionApprovalCompletionStatusDto requisitionApprovalCompletionStatusDto;
  private List<RequisitionApprovalCompletionStatusDto> requisitionApprovalCompletionStatusDtos;
  private PaginatedResponse<RequisitionApprovalCompletionStatusDto> requisitionApprovalCompletionStatusDtoPaginatedResponse;


  private  List<Map<String, Object>> mapBasedReportList;

  private  List<RequisitionLayerWrapperDto> requisitionLayerWrapperDtos;


  private RequisitionReplacementPersonDto requisitionReplacementPersonDto;
  private List<RequisitionReplacementPersonDto> requisitionReplacementPersonDtos;
  private PaginatedResponse<RequisitionReplacementPersonDto> requisitionReplacementPersonDtoPaginatedResponse;

  private ExpertiseEmployeeCategoryInfoDto expertiseEmployeeCategoryInfoDto;
  private List<ExpertiseEmployeeCategoryInfoDto> expertiseEmployeeCategoryInfoDtos;
  private PaginatedResponse<ExpertiseEmployeeCategoryInfoDto> expertiseEmployeeCategoryInfoDtoPaginatedResponse;
}
