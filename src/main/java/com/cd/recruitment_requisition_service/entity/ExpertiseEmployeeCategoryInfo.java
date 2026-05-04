package com.cd.recruitment_requisition_service.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "req_expertise_employee_category")
@Getter
@Setter
public class ExpertiseEmployeeCategoryInfo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eeci_seq")
    @SequenceGenerator(name = "eeci_seq", sequenceName = "expertise_employee_category_info_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    @JsonManagedReference
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    private String employeeCategoryName;
    private String employeeCategoryCode;

    // List of Expertise under this Category
    @OneToMany(mappedBy = "employeeCategoryInfo", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonBackReference
    private List<RequisitionAreaOfExpertise> areaOfExpertises = new ArrayList<>();
}
