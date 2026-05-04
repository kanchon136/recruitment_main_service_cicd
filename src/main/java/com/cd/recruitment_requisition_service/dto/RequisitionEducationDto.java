package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RequisitionEducationDto extends BaseEntityDto {

    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String levelOfEducationCode;

    private String levelOfEducationName;

    private String educationDegreeCode;

    private String educationDegreeName;

    private String branchOfStudyCode;

    private String branchOfStudyName;

}
