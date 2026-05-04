package com.cd.recruitment_requisition_service.dto;

import lombok.Data;

@Data
public class RequisitionMemberDto {
    private String memberCode;
    private String panelMember;
    private String approvedType;
    private Boolean isMandatoryAction;
    private String permissionLevels;
}
