package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionJustificationParam;
import com.cd.recruitment_requisition_service.service.RequisitionJustificationService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionJustification")
@Slf4j
public class RequisitionJustificationController {

    private final RequisitionJustificationService justificationService;

    public RequisitionJustificationController(RequisitionJustificationService justificationService) {
        this.justificationService = justificationService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionJustification( @RequestBody RequisitionJustificationParam param) {
        log.info("Request to save Requisition Justification for Master ID: {}", param.getRecruitmentRequisitionMasterId());
        BaseResponse response = justificationService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-all")
    public ResponseEntity<BaseResponse> saveAllRequisitionJustification( @RequestBody List<RequisitionJustificationParam> params) {
        log.info("Request to save batch Requisition Justification list size: {}", params != null ? params.size() : 0);
        BaseResponse response = justificationService.savedAll(params);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionJustification(@PathVariable Long id, @RequestBody RequisitionJustificationParam param) {
        log.info("Request to update Requisition Justification with ID: {}", id);
        BaseResponse response = justificationService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionJustification(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Justification with ID: {}", id);
        BaseResponse response = justificationService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionJustificationById(@PathVariable Long id) {
        log.info("Request to find Requisition Justification by ID: {}", id);
        BaseResponse response = justificationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionJustificationsByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Requisition Justifications for Master ID: {}", masterId);
        BaseResponse response = justificationService.findAllByRequisitionMasterId(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionJustificationsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Justifications with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = justificationService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionJustifications() {
        log.info("Request to find all active Requisition Justifications");
        BaseResponse response = justificationService.findAll();
        return ResponseEntity.ok(response);
    }
}
