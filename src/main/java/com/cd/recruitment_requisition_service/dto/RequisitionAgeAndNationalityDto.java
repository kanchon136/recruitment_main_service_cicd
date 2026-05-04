package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionAgeAndNationalityDto extends BaseEntityDto{
    private Long id;
    private Long recruitmentRequisitionMasterId;

    private String nationality;
    private String agePreferenceFrom;
    private String agePreferenceTo;
    private String additionalRequirement;
    private String additionalComment;
}
