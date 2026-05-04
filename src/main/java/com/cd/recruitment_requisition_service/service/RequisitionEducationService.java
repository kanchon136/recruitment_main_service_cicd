package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.RequisitionEducationParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface RequisitionEducationService extends BaseService<RequisitionEducationParam,Long> {

    BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentId);
}
