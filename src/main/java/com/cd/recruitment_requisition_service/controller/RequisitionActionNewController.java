package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.dto.RequisitionListViewDto;
import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.param.PIDResetParam;
import com.cd.recruitment_requisition_service.param.RequisitionActionParam;
import com.cd.recruitment_requisition_service.service.RequisitionActionNewService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionActionNew")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RequisitionActionNewController {

    private final RequisitionActionNewService requisitionActionNewService;

    public RequisitionActionNewController(RequisitionActionNewService requisitionActionNewService) {
        this.requisitionActionNewService = requisitionActionNewService;
    }

    /**
     * 1. Core Action Endpoint: Transitions a requisition from one stage to
     * another.
     */
    @PostMapping("/newTakeAction")
    public ResponseEntity<BaseResponse> takeAction(@RequestBody RequisitionActionParam request) {
        BaseResponse response = requisitionActionNewService.takeAction(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. Dashboard Listing Endpoint: Fetches "My Desk", "In Process",
     * "Approved", etc.
     */
    @GetMapping("/list")
    public ResponseEntity<List<RequisitionListViewDto>> getRequisitionsList(
            @RequestParam("userId") String userId,
            @RequestParam("filterType") String filterType) {

        List<RequisitionListViewDto> list = requisitionActionNewService.getRequisitionsForUser(userId, filterType);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/findAllByApprovedPlanId")
    public ResponseEntity<List<RequisitionListViewDto>> findAllRequisitionByApprovedPlanId(
            @RequestParam("approvedPlanId") String approvedPlanId) {

        List<RequisitionListViewDto> list = requisitionActionNewService.findAllRequisitionByApprovedPlanId(approvedPlanId);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/findAllByUserIdAndStatus")
    public ResponseEntity<List<RequisitionListViewDto>> getRequisitionsList(
            @RequestParam("userId") String userId,
            @RequestParam(value = "overallProcessStatusList")List<OverallProcessStatus> overallProcessStatusList) {

        List<RequisitionListViewDto> list = requisitionActionNewService.findAllRequisitionByOverralStatusAndUserID(userId, overallProcessStatusList);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin-tracking")
    public ResponseEntity<List<RequisitionListViewDto>> getAdminTracking(
            @RequestParam(value = "statuses") List<OverallProcessStatus> statuses) {
         List<OverallProcessStatus> updatedStatuses = new ArrayList<>(statuses);
        // ২. যদি PROCESSING থাকে কিন্তু DRAFT না থাকে, তবে DRAFT যোগ করা
        if (updatedStatuses.contains(OverallProcessStatus.PROCESSING) &&
                !updatedStatuses.contains(OverallProcessStatus.DRAFT)) {
            updatedStatuses.add(OverallProcessStatus.DRAFT);
        }
         List<RequisitionListViewDto> dtoList = requisitionActionNewService.getAdminTracking(updatedStatuses);

        return ResponseEntity.ok(dtoList);
    }

    /**
     * 3. Auto Next Step: Calculates who the requisition should move to next.
     */
    @GetMapping("/getAutoNextStepChannelDetails")
    public ResponseEntity<BaseResponse> getAutoNextStepChannelDetails(@RequestParam("masterId") Long masterId) {
        BaseResponse response = requisitionActionNewService.getAutoNextStepChannelDetails(masterId);
        return ResponseEntity.ok(response);
    }

    /**
     * 4. Previous Channels: Fetches the list of people who acted before the
     * current stage.
     */
    @GetMapping("/getPreviousLayerChannels")
    public ResponseEntity<BaseResponse> getPreviousLayerChannels(@RequestParam("masterId") Long masterId) {
        BaseResponse response = requisitionActionNewService.getPreviousLayerChannels(masterId);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. Skip Stage: Allows skipping an optional approval stage.
     */
    @Operation(summary = "Skip an optional approval layer/stage")
    @PostMapping("/skipStage")
    public ResponseEntity<BaseResponse> skipStage(@RequestParam("masterId") Long masterId,
            @RequestParam("layerToSkip") Integer layerToSkip) {
        BaseResponse response = requisitionActionNewService.skipStage(masterId, layerToSkip);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. Quick View Report: Summary for the "Quick View" approval modal.
     */
    @GetMapping("/getQuickViewReport")
    public ResponseEntity<BaseResponse> getQuickViewReport(@RequestParam("masterId") Long masterId) {
        BaseResponse response = requisitionActionNewService.getQuickViewReport(masterId);
        return ResponseEntity.ok(response);
    }

    /**
     * 7. Total Activity Log: Full history of actions.
     */
    @GetMapping("/getTotalActivityLogReport")
    public ResponseEntity<BaseResponse> getTotalActivityLogReport(@RequestParam("masterId") Long masterId) {
        BaseResponse response = requisitionActionNewService.getTotalActivityLogReport(masterId);
        return ResponseEntity.ok(response);
    }

    /**
     * 8. Reset PID: Internal helper for resetting initiation dates.
     */
    @PostMapping("/resetPid")
    @Hidden
    public ResponseEntity<BaseResponse> resetProcessInitiationDate(@RequestBody PIDResetParam request) {
        BaseResponse response = requisitionActionNewService.resetProcessInitiationDate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 9. Requisition Movement History: Audit trail.
     */
    @Hidden
    @GetMapping("/history/{requisitionId}")
    public ResponseEntity<BaseResponse> getRequisitionHistory(@PathVariable("requisitionId") Long requisitionId) {
        BaseResponse response = requisitionActionNewService.getRequisitionHistory(requisitionId);
        return ResponseEntity.ok(response);
    }

    /**
     * 10. All Actions List: Raw action logs.
     */
    @Hidden
    @GetMapping("/actions/{requisitionId}")
    public ResponseEntity<BaseResponse> getAllRequisitionActions(@PathVariable("requisitionId") Long requisitionId) {
        BaseResponse response = requisitionActionNewService.getAllRequisitionActions(requisitionId);
        return ResponseEntity.ok(response);
    }

    /**
     * 11. Group Action Details: Fetches parallel action details for a specific
     * layer.
     */
    @Hidden
    @GetMapping("/{masterId}/group-actions")
    public ResponseEntity<BaseResponse> getLayerGroupActionDetails(@PathVariable("masterId") Long masterId,
            @RequestParam("layerPosition") Integer layerPosition) {
        BaseResponse response = requisitionActionNewService.getGroupActionDetailsByLayer(masterId, layerPosition);
        return ResponseEntity.ok(response);
    }
}
