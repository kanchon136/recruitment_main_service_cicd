package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionSupportingDocumentsChildDto extends BaseEntityDto{
     private Long id;
  // private RequisitionSupportingDocumentsMaster requisitionSupportingDocumentsMaster;
     private Long requisitionSupportingDocumentsMasterId;
     private String fileName;
     private String filePath;
     private Long fileSize;
     private  String contentType;
     private LocalDateTime uploadDateTime;


}
