package com.cd.recruitment_requisition_service.param;

import com.cd.recruitment_requisition_service.enums.AuthorizationType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionApprovedChannelParam extends BaseParam {

    private Long id;

     private Long recruitmentRequisitionMasterId;

      private String memberCode; // previous was code

      private String processPanelCode;

     private String processTitle;

     private Integer layerPosition;

     private String approvedType;

     private String panelMember;  // previous was member

     private String designation;

     private AuthorizationType authorizationType;

     private Boolean isMandatoryAction; // that mean member action required in the same layer;

     private Boolean isStageMandatory;

     private String permissionLevels;
}
