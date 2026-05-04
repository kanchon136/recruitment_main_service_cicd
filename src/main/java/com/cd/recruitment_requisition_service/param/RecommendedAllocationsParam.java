package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecommendedAllocationsParam extends BaseParam{

    private Long id;

    private Long requisitionGroupActionId;

    private Long recruitmentRequisitionMasterId;

    private String approvedPlanId;
    private LocalDate lastPlacementDate;

    private String shiftAllocationCode;
    private String shiftAllocationName;

    private Integer recomendedHeadCount;

    private String personalSubAreaCode;
    private String personalSubAreaName;

    private String orgaUnitCode;
    private String orgaUnitName;

    private boolean anyPlanApproved;
}
