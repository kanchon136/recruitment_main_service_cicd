package com.cd.recruitment_requisition_service.param;
import com.cd.recruitment_requisition_service.dto.RequisitionAreaOfExpertiseSkillDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequisitionAreaOfExpertiseParam extends BaseParam{


    private Long id;
    private Long employeeCategoryInfoId;

    private String type;

    private String experienceCode;
    private String experienceName;

    private Integer experienceInYearFrom;
    private Integer experienceInYearTo;

    private List<RequisitionAreaOfExpertiseSkillParam>  requisitionAreaOfExpertiseSkillParams = new ArrayList<>();
}
