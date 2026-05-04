package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.ExpertiseEmployeeCategoryInfoParam;
import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseParam;
import com.cd.recruitment_requisition_service.service.RequisitionAreaOfExpertiseService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionAreaOfExpertise")
@Slf4j
public class RequisitionAreaOfExpertiseController {

    private final RequisitionAreaOfExpertiseService expertiseService;

    public RequisitionAreaOfExpertiseController(RequisitionAreaOfExpertiseService expertiseService) {
        this.expertiseService = expertiseService;
    }

    // New 3-Tier Save (Category -> Expertise -> Skill)
    @PostMapping("/save")
    public ResponseEntity<BaseResponse> save(@RequestBody ExpertiseEmployeeCategoryInfoParam param) {
        log.info("Request to save new Expertise Employee Category Info with nested details.");
        BaseResponse response = expertiseService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<BaseResponse> saveAll(@RequestBody List<RequisitionAreaOfExpertiseParam> param) {
        log.info("Request to save a list of Requisition Area of Expertise.");
        BaseResponse response = expertiseService.saveAll(param);
        return ResponseEntity.ok(response);
    }

    // Updated update method with new Param
    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody ExpertiseEmployeeCategoryInfoParam param) {
        log.info("Request to update Expertise Employee Category Info ID: {}", id);
        BaseResponse response = expertiseService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category-info/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
        log.info("Request to find Category Info by ID: {}", id);
        BaseResponse response = expertiseService.findById(id);
        return ResponseEntity.ok(response);
    }

    // Modified Delete to support Category, Expertise, or Skill level deletion
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> deleteById(
            @RequestParam(name = "categoryId",required = false) Long categoryId,
            @RequestParam(name = "expertiseId", required = false) Long expertiseId,
            @RequestParam(name = "skillId", required = false) Long skillId) {

        log.warn("Delete request - Category ID: {}, Expertise ID: {}, Skill ID: {}", categoryId, expertiseId, skillId);
        BaseResponse response = expertiseService.deleteById(categoryId, expertiseId, skillId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findByMasterId/{masterId}")
    public ResponseEntity<BaseResponse> findAllByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Details for Requisition Master ID: {}", masterId);
        BaseResponse response = expertiseService.findAllByMasterId(masterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    public ResponseEntity<BaseResponse> findAllWithPagination(@RequestParam(defaultValue = "0") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Request to find all Details with Pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = expertiseService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> findAll() {
        log.info("Request to find all Expertise Details.");
        BaseResponse response = expertiseService.findAll();
        return ResponseEntity.ok(response);
    }
}
