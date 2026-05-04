package com.cd.recruitment_requisition_service.entity;


import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruitment_requisition_master")
@Getter
@Setter
public class RecruitmentRequisitionMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rrm_seq")
    @SequenceGenerator(name = "rrm_seq", sequenceName = "requisition_master_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "requisition_code", unique = true, length = 100) // 50
    private String requisitionCode;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "user_name", length = 200)
    private String userName;

    @Column(name = "is_plan_based")
    private Boolean isPlanBased = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "requisition_type", length = 100) // 50
    private RequisitionType requisitionType;

    @Column(name = "process_initiation_date")
    private LocalDate processInitiationDate;  // URS: PID Date (Changed to LocalDateTime)

    @Column(name = "position_name", length = 200)
    private String positionName;

    @Column(name = "demand_in_requisition")
    private Integer demandInRequisition;

    @Column(name = "target_hire_date")
    private LocalDate targetHireDate;

    @Column(name = "salary_range_from")
    private Double salaryRangeFrom;

    @Column(name = "salary_range_to")
    private Double salaryRangeTo;

    @Column(name = "reporting_to", length = 200)
    private String reportingTo;

    private Integer returnToLayer; // new field for who is backed it

    @Column(name = "company_code", length = 100)
    private String companyCode;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "employee_category_code", length = 100)
    private String employeeCategoryCode;

    @Column(name = "employee_category_name", length = 200)
    private String employeeCategoryName;

    @Column(name = "employee_sub_group_code", length = 100)
    private String employeeSubGroupCode;

    @Column(name = "employee_sub_group_name", length = 200)
    private String employeeSubGroupName;

    @Column(name = "personal_area_code", length = 100)
    private String personalAreaCode;

    @Column(name = "personal_area_name", length = 200)
    private String personalAreaName;

    @Column(name = "workplace_code", length = 100)
    private String workplaceCode;

    @Column(name = "workplace_name", length = 200)
    private String workplaceName;

    @Column(name = "position_code", length = 100)
    private String positionCode;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus currentStatus;

    // --- URS/Approval Tracking Fields ---
    @Column(name = "current_rolee", length = 100)
    private String currentRole; // NEW: Role that took the last action

    @Column(name = "next_role", length = 100)
    private String nextRole; // NEW: Role responsible for the next action

    @Column(name = "business_unit", length = 100)
    private String businessUnit; // NEW: For Finance HOD skip logic (Sales & Distribution check)

    //  NEW: Current Layer Position (For Parallel Approval)
    @Column(name = "current_layer_position")
    private Integer currentLayerPosition=0;

    @Column(name = "last_acted_by", length = 100)
    private String lastActedBy;

     // Stores the role of the user who took the latest action.
    @Column(name = "last_acted_role", length = 100)
    private String lastActedRole;

    //Stores the remarks/opinion from the latest action (Last Member's Opinion/ProposalProposal).

    @Column(name = "last_action_remarks", columnDefinition = "TEXT")
    private String lastActionRemarks;

     // Stores the type of the latest action taken (e.g., REVIEW, RECOMMEND, BACK).
    @Enumerated(EnumType.STRING)
    @Column(name = "last_action_type", length = 100) // 50
    private RequisitionActionType lastActionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_process_status", length = 100) // 50
    private OverallProcessStatus overallProcessStatus;

    // --- Bi-Directional Relations (One-to-Many to all Detail/Audit Entities) ---
    @JsonBackReference
    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionJobDescription> jobDescriptions = new ArrayList<>();
//
//    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<RequisitionAreaOfExpertise> areasOfExpertise = new ArrayList<>();


    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<ExpertiseEmployeeCategoryInfo> expertiseEmployeeCategoryInfos = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionEducation> educations = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionRequirementsAllocation> allocations = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionApprovedChannel> approvalChannels = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionSupportingDocumentsMaster> documents = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionIndustry> requisitionIndustries = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionAdditionalRequirements> requisitionAdditionalRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionAgeAndNationality> requisitionAgeAndNationalities = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionJustification> requisitionJustifications = new ArrayList<>();

    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionPendingAction> pendingActions = new ArrayList<>(); // @OneToMany অবশ্যই লাগবে


    @OneToMany(mappedBy = "recruitmentRequisitionMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<RequisitionReplacementPerson> replacementPersons = new ArrayList<>();
}
