package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.enums.AuthorizationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionApprovedChannelDto extends BaseEntityDto{
    private Long id;
    private Long recruitmentRequisitionMasterId;

    private String MemberCode;
    private String processTitle;
    private String processPanelCode;
    private Integer layerPosition;
    private String approvedType;
    private String panelMember;
    private String designation;
    private AuthorizationType authorizationType;
    private Boolean isMandatoryAction;
    private Boolean isStageMandatory;
    private String permissionLevels;
    private Boolean isSkiped;


}
