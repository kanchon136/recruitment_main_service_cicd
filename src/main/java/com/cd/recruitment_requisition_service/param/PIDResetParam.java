package com.cd.recruitment_requisition_service.param;

import lombok.Data;

@Data
public class PIDResetParam {
    private Long masterRequisitionId;
    private String actedBy;
    private String actedRole; // e.g., "Sr. Admin" or "Raiser"
    private String remarks;
    private String ipAddress;
}
