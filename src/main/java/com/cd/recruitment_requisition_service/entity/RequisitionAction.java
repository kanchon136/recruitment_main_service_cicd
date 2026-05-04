package com.cd.recruitment_requisition_service.entity;

import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "requisition_action")
@Getter
@Setter
//@Builder
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionAction  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisition_action_seq")
    @SequenceGenerator(name = "requisition_action_seq", sequenceName = "requisition_action_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id")
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster; // Master Entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_channel_id")
    private RequisitionApprovedChannel approvedChannel;

    @Column(name = "acted_by", length = 100)
    private String actedBy;

    @Column(name = "acted_role", length = 100)
    private String actedRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50)
    private RequisitionActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 50)
    private RequisitionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 50)
    private RequisitionStatus newStatus;

    @Column(name = "from_stage", length = 100)
    private String fromStage;

    @Column(name = "to_stage", length = 100)
    private String toStage;

    @Column(name = "from_layer_position")
    private Integer fromLayerPosition;

    @Column(name = "to_layer_position")
    private Integer toLayerPosition;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "action_date_time")
    private LocalDateTime actionDateTime;

    @Column(name = "action_source", length = 50)
    private String actionSource;
}
