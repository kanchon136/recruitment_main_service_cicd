package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.entity.ExpertiseEmployeeCategoryInfo;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertiseSkill;
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
public class RequisitionAreaOfExpertiseDto  extends BaseEntityDto{

 private Long id;
 private Long employeeCategoryInfoId;

  private String type;

 private String experienceCode;
 private String experienceName;

 private Integer experienceInYearFrom;
 private Integer experienceInYearTo;

 private List<RequisitionAreaOfExpertiseSkillDto> requisitionAreaOfExpertiseSkillDtos = new ArrayList<>();
}
