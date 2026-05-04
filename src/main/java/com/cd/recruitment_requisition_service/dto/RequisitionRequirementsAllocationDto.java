package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionRequirementsAllocationDto extends BaseEntityDto {
    private Long id;
    private Long recruitmentRequisitionMasterId;
    private String approvedPlanId;

    private String shiftAllocationCode ;
    private String shiftAllocationName ;

    private Integer noOfRequirements;
    private LocalDate lastPlacementDate;

    private String personalSubAreaCode;
    private String personalSubAreaName;

    private String orgaUnitCode;
    private String orgaUnitName;

    private boolean anyPlanApproved;

}
