package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_area_of_expertise_skill")
@Getter
@Setter
public class RequisitionAreaOfExpertiseSkill extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "raoes_seq")
    @SequenceGenerator(name = "raoes_seq", sequenceName = "requisition_area_of_expertise_skill_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_area_of_expertise_id", nullable = false)
    @JsonManagedReference
    private RequisitionAreaOfExpertise requisitionAreaOfExpertise;

    @Column(name = "skill_name", nullable = false, length = 255)
    private String skillName;

    private String skillCode;

 }
