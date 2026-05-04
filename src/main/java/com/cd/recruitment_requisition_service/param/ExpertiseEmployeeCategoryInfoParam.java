package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExpertiseEmployeeCategoryInfoParam extends BaseParam{

    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String employeeCategoryName;
    private String employeeCategoryCode;

    private List<RequisitionAreaOfExpertiseParam>   requisitionAreaOfExpertiseParams = new ArrayList<>();


}
