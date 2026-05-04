package com.cd.recruitment_requisition_service.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_replacement_person")
@Getter
@Setter
public class RequisitionReplacementPerson extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rrp_seq")
    @SequenceGenerator(name = "rrp_seq", sequenceName = "replacement_person_seq", allocationSize = 1)
    private Long id;

    private String employeeId;
    private String employeeName;
    private String designation;
    private String departmentName;
    private String departmentCode;

    // --- নতুন ফিল্ডসমূহ ---
    private String lastRequisitionId;   // Replacing Requisition ID (Master Code)
    private String employmentStatus;    // Param থেকে আসবে (Active/Withdrawn)
    private String opinion;             // Group Action এর Remarks থেকে আসবে
    private String currentProposedStatus; // কার ডেস্কে বা কি অবস্থায় ছিল
    private Integer approvalFrequency = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id")
    @JsonManagedReference
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rrp_seq")
//    @SequenceGenerator(name = "rrp_seq", sequenceName = "replacement_person_seq", allocationSize = 1)
//    private Long id;
//
//    private String employeeId;
//    private String employeeName;
//    private String designation;
//    private String departmentName;
//    private String departmentCode;
//    private String requisitionId; // it field will be  value insert  when requisition is final approved;
//    private Integer approvalFrequency = 0; // এই এমপ্লয়ি এ পর্যন্ত কতবার রিপ্লেস হলো
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "recruitment_requisition_master_id")
//    @JsonManagedReference
//    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;
}
