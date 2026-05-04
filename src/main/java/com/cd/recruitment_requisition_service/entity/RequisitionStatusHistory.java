package com.cd.recruitment_requisition_service.entity;

import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "requisition_status_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
//@Builder
@SuperBuilder
public class RequisitionStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requisition_status_history_seq")
    @SequenceGenerator(name = "requisition_status_history_seq", sequenceName = "requisition_status_history_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id")
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster; // Master Entity

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 50)
    private RequisitionStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", length = 50)
    private RequisitionStatus toStatus;

    @Column(name = "from_role", length = 100)
    private String fromRole;

    @Column(name = "to_role", length = 100)
    private String toRole;

    @Column(name = "acted_by", length = 100)
    private String actedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50)
    private RequisitionActionType actionType;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "action_time")
    private LocalDateTime actionTime;

}
