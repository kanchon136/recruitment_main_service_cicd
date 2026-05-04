package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionRequirementsAllocationParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

import java.util.List;

public interface RequisitionRequirementsAllocationService extends BaseService<RequisitionRequirementsAllocationParam,Long> {

    BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentId);
    BaseResponse saveAll(List<RequisitionRequirementsAllocationParam> params);

}
