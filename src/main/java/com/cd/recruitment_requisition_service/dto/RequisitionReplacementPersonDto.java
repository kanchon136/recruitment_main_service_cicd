package com.cd.recruitment_requisition_service.dto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionReplacementPersonDto extends BaseEntityDto{

    private Long id;

    private String employeeId;
    private String employeeName;
    private String designation;
    private String departmentName;
    private String departmentCode;

    // --- নতুন ফিল্ডসমূহ ---
    private String lastRequisitionId;   // Replacing Requisition ID (Master Code)
    private String employmentStatus;    // Param থেকে আসবে (Active/Withdrawn)
    private String opinion;             // Group Action এর Remarks থেকে আসবে
    private String currentProposedStatus; // কার ডেস্কে বা কি অবস্থায় ছিল
    private Integer approvalFrequency = 0;

    private Long recruitmentRequisitionMasterId;

}
