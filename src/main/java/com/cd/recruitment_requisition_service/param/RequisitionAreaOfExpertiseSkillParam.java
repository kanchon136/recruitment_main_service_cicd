package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionAreaOfExpertiseSkillParam extends BaseParam{


    private Long id;

    private Long requisitionAreaOfExpertiseId;

    private String skillName;

    private String skillCode;
}
