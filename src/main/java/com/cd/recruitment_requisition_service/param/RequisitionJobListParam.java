package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionJobListParam  extends BaseParam{

    private Long id;
    private String jobListName;
    private String jobListCode;

     private List<RequisitionJobTaskParam> requisitionJobTaskParams;
}
