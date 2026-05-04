package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "requisition_requirements_allocation")
@Getter
@Setter
public class RequisitionRequirementsAllocation extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rra_seq")
    @SequenceGenerator(name = "rra_seq", sequenceName = "requisition_requirements_allocation_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rra_requisition_master"))
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    private String approvedPlanId;

    private LocalDate lastPlacementDate;
    @Column(name = "shift_allocation", length = 100)
    private String shiftAllocationCode ;

    private String shiftAllocationName ;



    @Column(name = "no_of_requirements")
    private Integer noOfRequirements;

    @Column(name = "personal_sub_area", length = 100)
    private String personalSubAreaCode;
    private String personalSubAreaName;

    private String orgaUnitCode;
    private String orgaUnitName;

    private boolean anyPlanApproved;

 }
