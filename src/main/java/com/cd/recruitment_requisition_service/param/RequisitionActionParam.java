package com.cd.recruitment_requisition_service.param;

import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionActionParam extends BaseParam{

    private Long masterRequisitionId;
    private RequisitionActionType actionType;
    private String actedBy;
    private String actedRole;
    private String actedUserName;
    private String remarks;
    private String reasonCode;

    private String selectedNextUserId;
    private Integer selectedNextLayer;

    // NEW: Recommended headcount input by the user
    private Integer recommendedHeadcount;

    private Integer targetLayerPosition; // Used for BACK action
    private String ipAddress;
   // private String actionSource; // "WEB" or "OFFLINE_UPLOAD"

    // new field for every Action status check just updatable
   // private String lastActedBy;

   // private String lastActedRole;
   // private String lastActionRemarks;

  //  private RequisitionActionType lastActionType;

    private List<RecommendedAllocationsParam> recommendedAllocationsParams;
}
