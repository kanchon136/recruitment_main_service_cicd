package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertise;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
public class ExpertiseEmployeeCategoryInfoDto extends BaseEntityDto{

    private Long id;

    private Long recruitmentRequisitionMasterId;

    private String employeeCategoryName;
    private String employeeCategoryCode;

    private List<RequisitionAreaOfExpertiseDto>  requisitionAreaOfExpertiseDtos = new ArrayList<>();
}
