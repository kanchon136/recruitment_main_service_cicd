package com.cd.recruitment_requisition_service.dto;


import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitionApprovalCompletionStatusDto {

    private Long masterRequisitionId;
    private String requisitionCode;

    /** Indicates if the Requisition has reached the FINAL_APPROVED status. */
    private boolean isApproved;

    /** Indicates if the Requisition process has reached any terminal status (APPROVED, REJECTED, WITHDRAWN). */
    private boolean isTerminalStatus;

    /** The current final status of the requisition. */
    private RequisitionStatus finalStatus;

    private LocalDateTime finalActionDate;
}
