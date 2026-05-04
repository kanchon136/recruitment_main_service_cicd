package com.cd.recruitment_requisition_service.param;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionEducationParam extends BaseParam {
    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String levelOfEducationCode;

    private String levelOfEducationName;

    private String educationDegreeCode;

    private String educationDegreeName;

    private String branchOfStudyCode;

    private String branchOfStudyName;


}
