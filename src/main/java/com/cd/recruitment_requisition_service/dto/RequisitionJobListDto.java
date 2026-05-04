package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.entity.BaseEntity;
import com.cd.recruitment_requisition_service.param.RequisitionJobTaskParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionJobListDto extends BaseEntityDto {
    private Long id;
    private String jobListName;
    private String jobListCode;

    private List<RequisitionJobTaskDto> requisitionJobTaskDtos;
}
