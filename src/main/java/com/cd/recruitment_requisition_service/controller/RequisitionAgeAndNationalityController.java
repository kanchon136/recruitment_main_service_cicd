package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionAgeAndNationalityParam;
import com.cd.recruitment_requisition_service.service.RequisitionAgeAndNationalityService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionAgeAndNationality")
@Slf4j
public class RequisitionAgeAndNationalityController {

    private final RequisitionAgeAndNationalityService nationalityService;

    public RequisitionAgeAndNationalityController(RequisitionAgeAndNationalityService nationalityService) {
        this.nationalityService = nationalityService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionAgeAndNationality(@RequestBody RequisitionAgeAndNationalityParam param) {
        log.info("Request to save Requisition Age And Nationality for Master ID: {}", param.getRecruitmentRequisitionMasterId());
        BaseResponse response = nationalityService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-all")
    public ResponseEntity<BaseResponse> saveAllRequisitionAgeAndNationality( @RequestBody List<RequisitionAgeAndNationalityParam> params) {
        log.info("Request to save batch Requisition Age And Nationality list size: {}", params != null ? params.size() : 0);
        BaseResponse response = nationalityService.savedAll(params);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionAgeAndNationality(@PathVariable Long id, @RequestBody RequisitionAgeAndNationalityParam param) {
        log.info("Request to update Requisition Age And Nationality with ID: {}", id);
        BaseResponse response = nationalityService.update(id, param);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionAgeAndNationality(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Age And Nationality with ID: {}", id);
        BaseResponse response = nationalityService.deleteById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionAgeAndNationalityById(@PathVariable Long id) {
        log.info("Request to find Requisition Age And Nationality by ID: {}", id);
        BaseResponse response = nationalityService.findById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionAgeAndNationalitiesByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Requisition Age And Nationalities for Master ID: {}", masterId);
        BaseResponse response = nationalityService.findAllByParentId(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionAgeAndNationalitiesPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Age And Nationalities with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = nationalityService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionAgeAndNationalities() {
        log.info("Request to find all active Requisition Age And Nationalities");
        BaseResponse response = nationalityService.findAll();
        return ResponseEntity.ok(response);
    }


}
