package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionIndustryDto extends BaseEntityDto{

    private Long id;
    private Long recruitmentRequisitionMasterId;
    private String industryCode;
    private String industryName;
}
