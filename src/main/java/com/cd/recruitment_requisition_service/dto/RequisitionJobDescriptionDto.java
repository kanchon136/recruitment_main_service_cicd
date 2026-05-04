package com.cd.recruitment_requisition_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionJobDescriptionDto extends BaseEntityDto {
    private Long id;
    // private RecruitmentRequisitionMasterDto recruitmentRequisitionMasterDto;
    private Long recruitmentRequisitionMasterId;
    private String jobDescription;

    private List<RequisitionJobListDto> requisitionJobListDtos = new ArrayList<>();
}
