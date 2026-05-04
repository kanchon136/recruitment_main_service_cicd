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
@Table(name = "requisition_job_list")
@Getter
@Setter
public class RequisitionJobList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rjd_seq_list")
    @SequenceGenerator(name = "rjd_seq_list", sequenceName = "requisition_job_list_seq", allocationSize = 1)
    private Long id;

    private String jobListName;
    private String jobListCode;

    // Bi-directional with JobDescription
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_job_description_id", nullable = false)
    @JsonManagedReference
    private RequisitionJobDescription requisitionJobDescription;

    // Bi-directional with JobTask
    @JsonBackReference
    @OneToMany(mappedBy = "requisitionJobList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequisitionJobTask> tasks = new ArrayList<>();


}
