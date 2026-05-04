package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_justification")
@Getter
@Setter
public class RequisitionJustification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_justification_seq")
    @SequenceGenerator(name = "req_justification_seq", sequenceName = "requisition_justification_seq", allocationSize = 1)
    private Long id;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "details", length = 1000)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;
}
