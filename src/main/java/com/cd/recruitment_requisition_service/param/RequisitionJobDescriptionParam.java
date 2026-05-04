package com.cd.recruitment_requisition_service.param;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionJobDescriptionParam  extends BaseParam{

    private Long id;
    private Long recruitmentRequisitionMasterId;
    private String jobDescription;

     private List<RequisitionJobListParam> requisitionJobListParams;
}
