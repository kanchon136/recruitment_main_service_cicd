package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_job_task")
@Getter
@Setter
public class RequisitionJobTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rjt_seq")
    @SequenceGenerator(name = "rjt_seq", sequenceName = "requisition_job_task_seq", allocationSize = 1)
    private Long id;

    // Bi-directional with JobList
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_job_list_id", nullable = false)
    @JsonManagedReference
    private RequisitionJobList requisitionJobList;

    @Column(name = "job_task", nullable = false, columnDefinition = "TEXT")
    private String jobTask;


}
