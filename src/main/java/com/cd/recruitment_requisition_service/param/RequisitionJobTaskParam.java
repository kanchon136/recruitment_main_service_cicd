package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionJobTaskParam  extends BaseParam{

    private Long id;
    private String jobTask;
}
