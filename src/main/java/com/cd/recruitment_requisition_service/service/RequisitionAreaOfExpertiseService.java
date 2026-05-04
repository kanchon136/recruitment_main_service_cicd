package com.cd.recruitment_requisition_service.service;

 import com.cd.recruitment_requisition_service.param.ExpertiseEmployeeCategoryInfoParam;
 import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseParam;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;

 import java.util.List;

public interface RequisitionAreaOfExpertiseService extends BaseService<ExpertiseEmployeeCategoryInfoParam,Long> {

    BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentId);
    BaseResponse saveAll(List<RequisitionAreaOfExpertiseParam> requisitionAreaOfExpertiseParamList);
    BaseResponse deleteById(Long categoryId, Long expertiseId, Long skillId);
    BaseResponse findAllByMasterId(Long masterId);

}
