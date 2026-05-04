package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionSupportingDocumentsMasterDto extends BaseEntityDto {

    private Long id;

    //private RecruitmentRequisitionMaster recruitmentRequisitionMaster;
    private Long recruitmentRequisitionMasterId;

     private String documentTitle;

     private String note;

     private List<RequisitionSupportingDocumentsChildDto> files;
}
