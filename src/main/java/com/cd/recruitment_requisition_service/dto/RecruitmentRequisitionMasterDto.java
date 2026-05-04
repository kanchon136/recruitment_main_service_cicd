package com.cd.recruitment_requisition_service.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

 import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentRequisitionMasterDto extends BaseEntityDto {

    private Long id;
    private String requisitionCode;

    private String userId;

    private String userName;

    private Boolean isPlanBased = false;

    private RequisitionType requisitionType;

    private LocalDate processInitiationDate;

    private String positionName;

    private Integer demandInRequisition;

    private LocalDate targetHireDate;

    private Double salaryRangeFrom;

    private Double salaryRangeTo;

    private String reportingTo;

    private String companyCode;
    private String companyName;

    private String employeeCategoryCode;
    private String employeeCategoryName;

    private String employeeSubGroupCode;
    private String employeeSubGroupName;

    private String personalAreaCode;
    private String personalAreaName;

    private String workplaceCode;
    private String workplaceName;

    private String positionCode;

    private RequisitionStatus currentStatus;

    private String currentRole;

    private String nextRole;

    private String businessUnit;

    private Integer currentLayerPosition = 0;

    private String lastActedBy;

    private String lastActedRole;


    private String lastActionRemarks;

    private RequisitionActionType lastActionType;

    private OverallProcessStatus overallProcessStatus;

    private List<RequisitionJobDescriptionDto> jobDescriptions = new ArrayList<>();

   // private List<RequisitionAreaOfExpertiseDto> areasOfExpertise = new ArrayList<>();

    private List<ExpertiseEmployeeCategoryInfoDto> areasOfExpertise = new ArrayList<>();

    private List<RequisitionEducationDto> educations = new ArrayList<>();

    private List<RequisitionRequirementsAllocationDto> allocations = new ArrayList<>();

    private List<RequisitionApprovedChannelDto> approvalChannels = new ArrayList<>();

    private List<RequisitionSupportingDocumentsMasterDto> documents = new ArrayList<>();

    private List<RequisitionIndustryDto> requisitionIndustries = new ArrayList<>();

    private List<RequisitionAdditionalRequirementsDto> requisitionAdditionalRequirements = new ArrayList<>();

    private List<RequisitionAgeAndNationalityDto> requisitionAgeAndNationalities = new ArrayList<>();

    private List<RequisitionJustificationDto> requisitionJustifications = new ArrayList<>();

    private List<RequisitionReplacementPersonDto> requisitionReplacementPersonDtos;

}
