package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionStatusHistoryDto  extends BaseEntityDto{
    private Long id;
    private Long masterRequisitionId;
    private RequisitionStatus fromStatus;
    private RequisitionStatus toStatus;
    private String fromRole;
    private String toRole;
    private String actedBy;
    private RequisitionActionType actionType;
    private String remarks;
    private LocalDateTime actionTime;
}
