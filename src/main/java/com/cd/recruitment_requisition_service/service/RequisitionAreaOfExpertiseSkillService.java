package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseSkillParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface RequisitionAreaOfExpertiseSkillService extends BaseService<RequisitionAreaOfExpertiseSkillParam,Long> {

    BaseResponse findAllByRecordStatusAndRequisitionAreaOfExpertise_idAndRecordStatus(Long parentId);

}
