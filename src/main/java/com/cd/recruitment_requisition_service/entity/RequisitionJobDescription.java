package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requisition_job_description")
@Getter
@Setter
public class RequisitionJobDescription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rjd_seq")
    @SequenceGenerator(name = "rjd_seq", sequenceName = "requisition_job_description_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    @JsonManagedReference
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    // Bi-directional with JobList
    @JsonBackReference
    @OneToMany(mappedBy = "requisitionJobDescription", cascade = CascadeType.ALL)
    private List<RequisitionJobList> requisitionJobLists = new ArrayList<>();

}
