package com.cd.recruitment_requisition_service.controller;


import com.cd.recruitment_requisition_service.param.RequisitionJobDescriptionParam;
import com.cd.recruitment_requisition_service.service.RequisitionJobDescriptionService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionJobDescription")
@Slf4j
public class RequisitionJobDescriptionController {

    private final RequisitionJobDescriptionService jobDescriptionService;

    // Constructor Injection Pattern
    public RequisitionJobDescriptionController(RequisitionJobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> save(@RequestBody RequisitionJobDescriptionParam param) {
        log.info("Request to save new Requisition Job Description detail.");
        BaseResponse response = jobDescriptionService.save(param);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/saveAll")
    public ResponseEntity<BaseResponse> saveAll(@RequestBody List<RequisitionJobDescriptionParam> param) {
        log.info("Request to save new Requisition Job Description detail.");
        BaseResponse response = jobDescriptionService.saveAll(param);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody RequisitionJobDescriptionParam param) {
        log.info("Request to update Requisition Job Description ID: {}", id);
        BaseResponse response = jobDescriptionService.update(id, param);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findById/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
        log.info("Request to find Requisition Job Description ID: {}", id);
        BaseResponse response = jobDescriptionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
            @RequestParam(required = false) Long descriptionId,
            @RequestParam(required = false) Long listId,
            @RequestParam(required = false) Long taskId) {

        log.warn("Delete request received - Description ID: {}, List ID: {}, Task ID: {}",
                descriptionId, listId, taskId);
        BaseResponse response = jobDescriptionService.deleteByIds(descriptionId, listId, taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByParentId/{parentMasterId}")
    public ResponseEntity<BaseResponse> findAllByMasterId(@PathVariable Long parentMasterId) {
        log.info("Request to find all Job Descriptions for Requisition Master ID: {}", parentMasterId);
        BaseResponse response = jobDescriptionService.findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(parentMasterId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findAllPaginate/paged")
    public ResponseEntity<BaseResponse> findAllWithPagination(@RequestParam(defaultValue = "0") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Request to find all Job Descriptions (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = jobDescriptionService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findAll")
    public ResponseEntity<BaseResponse> findAll() {
        log.info("Request to find all Job Descriptions.");
        BaseResponse response = jobDescriptionService.findAll();
        return ResponseEntity.ok(response);
    }
}
