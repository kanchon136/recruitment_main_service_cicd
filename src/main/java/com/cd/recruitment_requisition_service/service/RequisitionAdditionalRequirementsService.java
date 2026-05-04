package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionAdditionalRequirementsParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

import java.util.List;

public interface RequisitionAdditionalRequirementsService extends BaseService<RequisitionAdditionalRequirementsParam,Long>{

    BaseResponse findAllByRequisitionMasterId(Long parentId);
    BaseResponse savedAll (List<RequisitionAdditionalRequirementsParam> requisitionAdditionalRequirementsParams);
}
