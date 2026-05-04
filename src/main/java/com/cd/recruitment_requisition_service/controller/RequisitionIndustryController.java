package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionIndustryParam;
import com.cd.recruitment_requisition_service.service.RequisitionIndustryService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionIndustry")
@Slf4j
public class RequisitionIndustryController {

    private final RequisitionIndustryService industryService;

    public RequisitionIndustryController(RequisitionIndustryService industryService) {
        this.industryService = industryService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionIndustry( @RequestBody RequisitionIndustryParam param) {
        log.info("Request to save Requisition Industry: {}", param.getIndustryName());
        BaseResponse response = industryService.save(param);
        return  ResponseEntity.ok(response);
    }


    @PostMapping("/saveAll")
    public ResponseEntity<BaseResponse> saveRequisitionIndustryAll(@RequestBody List<RequisitionIndustryParam> param) {
        BaseResponse response = industryService.saveAll(param);
        return  ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionIndustry(@PathVariable Long id, @RequestBody RequisitionIndustryParam param) {
        log.info("Request to update Requisition Industry with ID: {}", id);
        BaseResponse response = industryService.update(id, param);
        return  ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionIndustry(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Industry with ID: {}", id);
        BaseResponse response = industryService.deleteById(id);
        return  ResponseEntity.ok(response);
    }
     @GetMapping("/findById/{id}")
    public ResponseEntity<BaseResponse> getRequisitionIndustryById(@PathVariable Long id) {
        log.info("Request to find Requisition Industry by ID: {}", id);
        BaseResponse response = industryService.findById(id);
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/findByMasterId/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionIndustriesByMasterId(@PathVariable("masterId") Long parentId) {
        log.info("Request to find all Requisition Industries for Master ID: {}", parentId);
        BaseResponse response = industryService.findAllByParentId(parentId);
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionIndustriesPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Industries with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = industryService.findAllWithPagination(pageNo, pageSize);
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionIndustries() {
        log.info("Request to find all active Requisition Industries");
        BaseResponse response = industryService.findAll();
        return  ResponseEntity.ok(response);
    }
}
