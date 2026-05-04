package com.cd.recruitment_requisition_service.param;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionSupportingDocumentsChildParam extends BaseParam{

    private Long id;

    private Long requisitionSupportingDocumentsMasterId;

    private String fileName;

    private String filePath;

}
