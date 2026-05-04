package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionReplacementPersonParam extends BaseParam {


    private Long id;

    private String employeeId;
    private String employeeName;
    private String designation;
    private String departmentName;
    private String departmentCode;

    private String lastRequisitionId;
    private String employmentStatus;
    private String opinion;
    private String currentProposedStatus;
    private Integer approvalFrequency = 0;

    private Long recruitmentRequisitionMasterId;


}
