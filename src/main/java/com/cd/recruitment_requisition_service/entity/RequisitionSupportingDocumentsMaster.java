package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requisition_supporting_documents_master")
@Getter
@Setter
public class RequisitionSupportingDocumentsMaster extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rsdm_seq")
    @SequenceGenerator(name = "rsdm_seq", sequenceName = "requisition_supporting_documents_master_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rsdm_requisition_master"))
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    @Column(name = "document_title", nullable = false, length = 255)
    private String documentTitle;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "requisitionSupportingDocumentsMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequisitionSupportingDocumentsChild> files = new ArrayList<>();

 }
