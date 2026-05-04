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
@Table(name = "requisition_area_of_expertise")
@Getter
@Setter
public class RequisitionAreaOfExpertise extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "raoe_seq")
    @SequenceGenerator(name = "raoe_seq", sequenceName = "requisition_area_of_expertise_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_category_info_id", nullable = false)
    @JsonManagedReference
    private ExpertiseEmployeeCategoryInfo employeeCategoryInfo;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    private String experienceCode;
    private String experienceName;

    private Integer experienceInYearFrom;
    private Integer experienceInYearTo;

    // List of Skills under this Expertise
    @OneToMany(mappedBy = "requisitionAreaOfExpertise", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonBackReference
    private List<RequisitionAreaOfExpertiseSkill> skills = new ArrayList<>();

 }
