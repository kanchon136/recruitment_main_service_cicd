package com.cd.recruitment_requisition_service.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionAgeAndNationalityParam extends BaseParam{
    private Long id;
    private Long recruitmentRequisitionMasterId;
    private String nationality;
    private String agePreferenceFrom;
    private String agePreferenceTo;
    private String additionalRequirement;
    private String additionalComment;
}
