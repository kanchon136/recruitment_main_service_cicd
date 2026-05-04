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
public class RequisitionActionDto extends BaseEntityDto{
    private Long id;
    private Long masterRequisitionId;
    private Long approvedChannelId;
    private String actedBy;
    private String actedRole;
    private RequisitionActionType actionType;
    private RequisitionStatus previousStatus;
    private RequisitionStatus newStatus;
    private Integer fromLayerPosition;
    private Integer toLayerPosition;
    private String remarks;
    private LocalDateTime actionDateTime;
    private String actionSource;
}
