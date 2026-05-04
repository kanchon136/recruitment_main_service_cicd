package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "requisition_recommended_allocations")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedAllocations extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recomended_seq")
    @SequenceGenerator(
            name = "recomended_seq",
            sequenceName = "recomended_allocation_sequence",
            allocationSize = 1
    )
    private Long id;

    // =========================
    // Link to Group Action (IMPORTANT)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requisition_group_action_id",
            nullable = false
    )
    @JsonIgnore
    private RequisitionGroupAction requisitionGroupAction;

    // =========================
    // Optional: direct master link (for reporting)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_master_id", nullable = false)
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    private String approvedPlanId;
    private LocalDate lastPlacementDate;

    private String shiftAllocationCode;
    private String shiftAllocationName;

    private Integer recomendedHeadCount;

    private String personalSubAreaCode;
    private String personalSubAreaName;

    private String orgaUnitCode;
    private String orgaUnitName;

    private boolean anyPlanApproved;
}
