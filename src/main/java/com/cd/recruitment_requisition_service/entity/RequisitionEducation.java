package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "requisition_education")
@Getter
@Setter
public class RequisitionEducation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "red_seq")
    @SequenceGenerator(name = "red_seq", sequenceName = "requisition_education_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false, foreignKey = @ForeignKey(name = "fk_red_requisition_master"))
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    // --- New fields from JSON ---

    @Column(name = "level_of_education_code", length = 50)
    private String levelOfEducationCode;

    @Column(name = "level_of_education_name", length = 255)
    private String levelOfEducationName;

    @Column(name = "education_degree_code", length = 50)
    private String educationDegreeCode;

    @Column(name = "education_degree_name", length = 255)
    private String educationDegreeName;

    @Column(name = "branch_of_study_code", length = 50)
    private String branchOfStudyCode;

    @Column(name = "branch_of_study_name", length = 255)
    private String branchOfStudyName;

 }
