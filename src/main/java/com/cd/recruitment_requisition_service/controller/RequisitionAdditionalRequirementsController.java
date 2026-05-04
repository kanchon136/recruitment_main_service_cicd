package com.cd.recruitment_requisition_service.controller;


import com.cd.recruitment_requisition_service.param.RequisitionAdditionalRequirementsParam;
import com.cd.recruitment_requisition_service.service.RequisitionAdditionalRequirementsService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionAdditionalRequirements")
@Slf4j
public class RequisitionAdditionalRequirementsController {
    private final RequisitionAdditionalRequirementsService requirementsService;

    public RequisitionAdditionalRequirementsController(RequisitionAdditionalRequirementsService requirementsService) {
        this.requirementsService = requirementsService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionAdditionalRequirements( @RequestBody RequisitionAdditionalRequirementsParam param) {
        log.info("Request to save Requisition Additional Requirement for Master ID: {}", param.getRecruitmentRequisitionMasterId());
        BaseResponse response = requirementsService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-all")
    public ResponseEntity<BaseResponse> saveAllRequisitionAdditionalRequirements( @RequestBody List<RequisitionAdditionalRequirementsParam> params) {
        log.info("Request to save batch Requisition Additional Requirement list size: {}", params != null ? params.size() : 0);
        // Calls the service method named 'savedAll'
        BaseResponse response = requirementsService.savedAll(params);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionAdditionalRequirements(@PathVariable Long id, @RequestBody RequisitionAdditionalRequirementsParam param) {
        log.info("Request to update Requisition Additional Requirement with ID: {}", id);
        BaseResponse response = requirementsService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionAdditionalRequirements(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Additional Requirement with ID: {}", id);
        BaseResponse response = requirementsService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionAdditionalRequirementsById(@PathVariable Long id) {
        log.info("Request to find Requisition Additional Requirement by ID: {}", id);
        BaseResponse response = requirementsService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionAdditionalRequirementsByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Requisition Additional Requirements for Master ID: {}", masterId);
        // Calls the service method named 'findAllByRequisitionMasterId'
        BaseResponse response = requirementsService.findAllByRequisitionMasterId(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionAdditionalRequirementsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Additional Requirements with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = requirementsService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionAdditionalRequirements() {
        log.info("Request to find all active Requisition Additional Requirements");
        BaseResponse response = requirementsService.findAll();
        return ResponseEntity.ok(response);
    }
}
