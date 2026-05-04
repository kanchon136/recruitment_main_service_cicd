package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionModuleCountDto {

    private Long requisitionMasterId;
    private String planCode;
    private String requisitionCode;

    private Integer planStructure;
    private Integer jobDescription;
    private Integer justification;
    private Integer additionalRequirement;
    private Integer education;
    private Integer areaOfExpertise;
    private Integer industry;
    private Integer nationality;
    private Integer supportingDocuments;
    private Integer approvedChannel;
    private Integer requirementsAllocation;
    private  Integer RequisitionReplacementPerson;
}
