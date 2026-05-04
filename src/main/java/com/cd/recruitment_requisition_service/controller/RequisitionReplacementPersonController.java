package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionReplacementPersonParam;
import com.cd.recruitment_requisition_service.service.RequisitionReplacementPersonService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionReplacementPerson")
@Slf4j
public class RequisitionReplacementPersonController {
    private final RequisitionReplacementPersonService replacementPersonService;

    public RequisitionReplacementPersonController(RequisitionReplacementPersonService replacementPersonService) {
        this.replacementPersonService = replacementPersonService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionReplacementPerson(@RequestBody RequisitionReplacementPersonParam param) {
        log.info("Request to save Requisition Replacement Person for Master ID: {}", param.getRecruitmentRequisitionMasterId());
        BaseResponse response = replacementPersonService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-all")
    public ResponseEntity<BaseResponse> saveAllRequisitionReplacementPerson(@RequestBody List<RequisitionReplacementPersonParam> params) {
        log.info("Request to save batch Requisition Replacement Person list size: {}", params != null ? params.size() : 0);
        BaseResponse response = replacementPersonService.saveAll(params);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionReplacementPerson(@PathVariable Long id, @RequestBody RequisitionReplacementPersonParam param) {
        log.info("Request to update Requisition Replacement Person with ID: {}", id);
        BaseResponse response = replacementPersonService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionReplacementPerson(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Replacement Person with ID: {}", id);
        BaseResponse response = replacementPersonService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionReplacementPersonById(@PathVariable Long id) {
        log.info("Request to find Requisition Replacement Person by ID: {}", id);
        BaseResponse response = replacementPersonService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionReplacementPersonsByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Requisition Replacement Persons for Master ID: {}", masterId);
        BaseResponse response = replacementPersonService.findAllByRequisitionMasterId(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionReplacementPersonsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Replacement Persons with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = replacementPersonService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionReplacementPersons() {
        log.info("Request to find all active Requisition Replacement Persons");
        BaseResponse response = replacementPersonService.findAll();
        return ResponseEntity.ok(response);
    }
}
