package com.cd.recruitment_requisition_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "requisition_supporting_documents_child")
@Getter
@Setter
public class RequisitionSupportingDocumentsChild extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rsdc_seq")
    @SequenceGenerator(name = "rsdc_seq", sequenceName = "requisition_supporting_documents_child_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_supporting_documents_master_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rsdc_supporting_documents_master"))
    @JsonIgnore
    private RequisitionSupportingDocumentsMaster requisitionSupportingDocumentsMaster;

    @Column(name = "file_name", length = 255)
    private String fileName;

     private String filePath;

    private Long fileSize;
    private  String contentType;

    @Column(nullable = false)
    private LocalDateTime uploadDateTime;

}

