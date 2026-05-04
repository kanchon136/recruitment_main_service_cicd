package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.param.RequisitionJobTaskParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface RequisitionJobTaskService extends BaseService<RequisitionJobTaskParam,Long> {

    BaseResponse findAllByRecordStatusAndRequisitionJobDescription_idAndRecordStatus(Long parentId);
}
