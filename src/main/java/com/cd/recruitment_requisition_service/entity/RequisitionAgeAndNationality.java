package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "req_age_and_nationality")
@Getter
@Setter
public class RequisitionAgeAndNationality extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "req_age_nationality_seq")
    @SequenceGenerator(name = "req_age_nationality_seq", sequenceName = "requisition_age_and_nationality_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    //@Column(name = "nationality", nullable = false, length = 100)
    @Column(name = "nationality", nullable = false, columnDefinition = "TEXT")
    private String nationality;

    @Column(name = "age_preference_from", length = 10)
    private String agePreferenceFrom;

    @Column(name = "age_preference_to", length = 10)
    private String agePreferenceTo;

    @Column(name = "additional_requirement", columnDefinition = "TEXT")
    private String additionalRequirement;

    @Column(name = "additional_comment", columnDefinition = "TEXT")
    private String additionalComment;
}
