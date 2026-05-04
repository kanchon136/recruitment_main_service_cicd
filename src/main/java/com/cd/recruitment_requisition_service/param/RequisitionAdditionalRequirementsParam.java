package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionAdditionalRequirementsParam extends BaseParam{
    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String additionalRequirement; // Requirement can be optional

    private String additionalComment;
}
