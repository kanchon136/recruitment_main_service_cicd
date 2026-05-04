package com.cd.recruitment_requisition_service.entity;

import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requisition_group_action")
@Getter
@Setter
//@Builder
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionGroupAction extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rga_seq")
    @SequenceGenerator(name = "rga_seq", sequenceName = "requisition_group_action_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    @Column(name = "acted_user_code")
    private String actedUserCode; //  Modified to Long for service logic

    private String actedUserName;

    @Column(name = "current_rolee", length = 100)
    private String currentRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50)
    private RequisitionActionType actionType; // Tracks APPROVE, REJECT, SEND

    // 🌟 NEW FIELD: User-wise recruitment recommendation (Tracking the proposal)
    @Column(name = "recommended_headcount")
    private Integer recommendedHeadcount;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "action_date_time")
    private LocalDateTime actionDateTime;

    private Integer layerPosition;

    // =========================
    // Allocation-wise Recommendation
    // =========================
    @OneToMany(
            mappedBy = "requisitionGroupAction",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<RecommendedAllocations> recommendedAllocations;


}
