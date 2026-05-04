package com.cd.recruitment_requisition_service.param;


import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecruitmentRequisitionMasterParam extends BaseParam {

    private Long id;
    private String requisitionCode;

    private String userId;

    private String userName;

    private Boolean isPlanBased = false;

    private RequisitionType requisitionType;

    private LocalDate processInitiationDate;

    private String positionName;

    private Integer demandInRequisition;

    private LocalDate targetHireDate;

    private Double salaryRangeFrom;

    private Double salaryRangeTo;

    private String companyCode;
    private String companyName;

    private String employeeCategoryCode;
    private String employeeCategoryName;

    private String employeeSubGroupCode;
    private String employeeSubGroupName;

    private String personalAreaCode;
    private String personalAreaName;

    private String workplaceCode;
    private String workplaceName;

    private String positionCode;

    private RequisitionStatus currentStatus;

    private String currentRole;

    private String nextRole;

    private String businessUnit;

    private Integer currentLayerPosition;

    private String lastActedBy;

    private String lastActedRole;

    private String reportingTo;


    private String lastActionRemarks;

     private RequisitionActionType lastActionType;

    //private OverallProcessStatus overallProcessStatus;

}
