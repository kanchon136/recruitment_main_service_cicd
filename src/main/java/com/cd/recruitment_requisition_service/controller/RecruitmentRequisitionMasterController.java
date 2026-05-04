package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.dto.RecruitmentRequisitionMasterDto;
import com.cd.recruitment_requisition_service.param.RecruitmentRequisitionMasterParam;
import com.cd.recruitment_requisition_service.service.RecruitmentRequisitionMasterService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.cd.recruitment_requisition_service.dto.RecruitmentRequisitionMasterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/requisition/requisitionRequisitionMaster")
@Slf4j
public class RecruitmentRequisitionMasterController {
    private final RecruitmentRequisitionMasterService requisitionService;

    public RecruitmentRequisitionMasterController(RecruitmentRequisitionMasterService requisitionService) {
        this.requisitionService = requisitionService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> save(@RequestBody RecruitmentRequisitionMasterParam param) {
        log.info("Request to save new Requisition.");
        BaseResponse response = requisitionService.save(param);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody RecruitmentRequisitionMasterParam param) {
        log.info("Request to update Requisition ID: {}", id);
        BaseResponse response = requisitionService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByRequisitionMaterIdWithAllReference/{masterId}")
    public ResponseEntity<BaseResponse> findByRequisitionMaterIdWithAllReference(@PathVariable Long masterId) {
        log.info("Request to find Requisition ID: {}", masterId);
        BaseResponse response = requisitionService.findByRequisitionMaterIdWithAllReference(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
        log.info("Request to find Requisition ID: {}", id);
        BaseResponse response = requisitionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> findAll(@RequestParam(defaultValue = "0") int pageNo,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Request to find all Requisitions (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = requisitionService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findAll")
    public ResponseEntity<BaseResponse> findAllWithoutPagination() {
        BaseResponse response = requisitionService.findAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteById(@PathVariable Long id) {
        log.warn("Request to delete Requisition ID: {}", id);
        BaseResponse response = requisitionService.deleteById(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/from-plan/{manpowerPlanningMasterId}")
    public ResponseEntity<BaseResponse> createFromPlan(@PathVariable Long manpowerPlanningMasterId,
                                                       @RequestParam String raiserId) {
        log.info("Request to create Requisition from Manpower Plan ID: {}", manpowerPlanningMasterId);
        BaseResponse response = requisitionService.createRequisitionFromPlan(manpowerPlanningMasterId, raiserId);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get Requisition Approval Completion Status",
            description = "Checks the Master Requisition status to determine if the approval process is complete (FINAL_APPROVED) or terminated (REJECTED/WITHDRAWN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved completion status",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Requisition ID not found")
    })
    @GetMapping("/completion-status/{requisitionMasterId}")
    public ResponseEntity<BaseResponse> getCompletionStatus(
            @Parameter(description = "ID of the Master Requisition", required = true)
            @PathVariable Long requisitionMasterId) {
        log.info("Request received to check approval completion status for Requisition ID: {}", requisitionMasterId);
        BaseResponse response = requisitionService.checkApprovalCompletion(requisitionMasterId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findAllInitialDataByUserId/{userId}")
    public ResponseEntity<BaseResponse> findAllWithoutPagination(@PathVariable String userId) {
        BaseResponse response = requisitionService.findAllByRecordStatusAndCurrentStatusAndUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overall-status/{status}")
    public ResponseEntity<List<RecruitmentRequisitionMasterDto>> getByOverallProcessStatus(
            @PathVariable String status,
            @RequestParam(required = false) String userId) {
        List<String> statusList = Arrays.asList(status.split(","));
        List<RecruitmentRequisitionMasterDto> responses = requisitionService.getByCurrentStatus(statusList, userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/search")
    public ResponseEntity<BaseResponse> searchRequisitions(@RequestParam("field") String field,
                                                           @RequestParam("value") String value) {
         BaseResponse response = requisitionService.searchRequisitions(field, value);

        return ResponseEntity.ok(response);
    }

}
