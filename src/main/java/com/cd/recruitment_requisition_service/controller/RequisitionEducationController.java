package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionEducationParam;
import com.cd.recruitment_requisition_service.service.RequisitionEducationService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requisition/requisitionEducation")
@Slf4j
public class RequisitionEducationController {

    private final RequisitionEducationService educationService;

     public RequisitionEducationController(RequisitionEducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping("/education")
    public ResponseEntity<BaseResponse> save(@RequestBody RequisitionEducationParam param) {
        log.info("Request to save new Requisition Education detail.");
        BaseResponse response = educationService.save(param);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/education/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody RequisitionEducationParam param) {
        log.info("Request to update Requisition Education ID: {}", id);
        BaseResponse response = educationService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/education/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
        log.info("Request to find Requisition Education ID: {}", id);
        BaseResponse response = educationService.findById(id);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/education/{id}")
    public ResponseEntity<BaseResponse> deleteById(@PathVariable Long id) {
        log.warn("Request to delete Requisition Education ID: {}", id);
        BaseResponse response = educationService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/{parentMasterId}/education")
    public ResponseEntity<BaseResponse> findAllByMasterId(@PathVariable Long parentMasterId) {
        log.info("Request to find all Education Details for Requisition Master ID: {}", parentMasterId);
        BaseResponse response = educationService.findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(parentMasterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/education/paged")
    public ResponseEntity<BaseResponse> findAllWithPagination(@RequestParam(defaultValue = "0") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Request to find all Education Details (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = educationService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/education/all")
    public ResponseEntity<BaseResponse> findAll() {
        log.info("Request to find all Education Details.");
        BaseResponse response = educationService.findAll();
        return ResponseEntity.ok(response);
    }
}
