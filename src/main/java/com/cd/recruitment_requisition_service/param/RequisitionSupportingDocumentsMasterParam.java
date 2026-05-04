package com.cd.recruitment_requisition_service.param;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionSupportingDocumentsMasterParam extends BaseParam{

    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String documentTitle;


    List<MultipartFile> files;



   // private List<RequisitionSupportingDocumentsChildParam> files;
}
