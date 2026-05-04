package com.cd.recruitment_requisition_service.service;

import com.cd.recruitment_requisition_service.dto.RequisitionListViewDto;
import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.param.PIDResetParam;
import com.cd.recruitment_requisition_service.param.RequisitionActionParam;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequisitionActionNewService {
    BaseResponse takeAction(RequisitionActionParam request);

    // 🌟 NEW: Dashboard Listing Method
    List<RequisitionListViewDto> getRequisitionsForUser(String userId, String filterType);

    List<RequisitionListViewDto> findAllRequisitionByOverralStatusAndUserID(String userId, List<OverallProcessStatus> overallProcessStatusList);

    //List<RequisitionListViewDto> findAllTrackingRequisitionsWithoutUser();

    List<RequisitionListViewDto> getAdminTracking(List<OverallProcessStatus> statusList);

    // Existing methods from your old controller snippet
    BaseResponse resetProcessInitiationDate(PIDResetParam request);
    BaseResponse getRequisitionHistory(Long requisitionId);
    BaseResponse getAllRequisitionActions(Long requisitionId);
    BaseResponse getGroupActionDetailsByLayer(Long masterId, Integer layerPosition);
    BaseResponse getAutoNextStepChannelDetails(Long masterId);
    BaseResponse getPreviousLayerChannels(Long masterId);
    BaseResponse skipStage(Long masterId, Integer layerToSkip);

    BaseResponse getQuickViewReport(Long masterId);
    BaseResponse getTotalActivityLogReport(Long masterId);

    List<RequisitionListViewDto> findAllRequisitionByApprovedPlanId( String approvedPlanId);
}
