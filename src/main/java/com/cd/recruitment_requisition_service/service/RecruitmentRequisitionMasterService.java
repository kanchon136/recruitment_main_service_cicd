package com.cd.recruitment_requisition_service.service;

import java.util.List;

import com.cd.recruitment_requisition_service.dto.RecruitmentRequisitionMasterDto;
import com.cd.recruitment_requisition_service.param.RecruitmentRequisitionMasterParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;

public interface RecruitmentRequisitionMasterService extends BaseService<RecruitmentRequisitionMasterParam,Long> {

    BaseResponse submitRequisition(Long requisitionId, String raiserId);
    BaseResponse approveRequisition(Long channelId, String approverId, String comments);
    BaseResponse rejectRequisition(Long channelId, String approverId, String comments);
    BaseResponse sendBackRequisition(Long channelId, String approverId, String comments);
    BaseResponse postponeRequisition(Long requisitionId, String actingUserId, String justification);
    BaseResponse withdrawRequisition(Long requisitionId, String actingUserId, String justification);

    // planning integration method
    BaseResponse createRequisitionFromPlan(Long manpowerPlanningMasterId, String raiserId);
    BaseResponse checkApprovalCompletion(Long masterRequisitionId);

    BaseResponse findByRequisitionMaterIdWithAllReference(Long requisitionMasterId);

    BaseResponse findAllByRecordStatusAndCurrentStatusAndUserId(String userId); // shown only for won stage when create the newly

    List<RecruitmentRequisitionMasterDto> getByCurrentStatus(List<String> statuses, String userId);

    BaseResponse searchRequisitions(String fieldName, String searchValue);

}
