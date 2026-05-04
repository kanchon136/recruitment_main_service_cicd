package com.cd.recruitment_requisition_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "requisition_pending_action")
@Getter
@Setter
//@Builder
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionPendingAction  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpa_seq")
    @SequenceGenerator(name = "rpa_seq", sequenceName = "requisition_pending_action_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    @Column(name = "pending_user_id", nullable = false)
    private String pendingUserId; // Target User ID

    private String pendingUserName;

    @Column(name = "layer_position", nullable = false)
    private Integer layerPosition; // Approval Layer

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true; // Mandatory/Optional Action

    @Column(name = "has_acted", nullable = false)
    private boolean hasActed = false; // Has user acted?

    @Column(name = "member_code", length = 255)
    private String memberCode;
}
