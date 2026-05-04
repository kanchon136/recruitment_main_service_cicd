package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_additional_requirements")
@Getter
@Setter
public class RequisitionAdditionalRequirements  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_add_req_seq")
    @SequenceGenerator(name = "req_add_req_seq", sequenceName = "requisition_additional_requirements_seq", allocationSize = 1)
    private Long id;

    @Column(name = "additional_requirement", length = 500)
    private String additionalRequirement;

    @Column(name = "additional_comment", length = 1000)
    private String additionalComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "req_requisition_master_id", nullable = false)
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;


}
