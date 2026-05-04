package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionRequirementsAllocationParam;
import com.cd.recruitment_requisition_service.service.RequisitionRequirementsAllocationService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionRequirementsAllocation")
@Slf4j
public class RequisitionRequirementsAllocationController {

    private final RequisitionRequirementsAllocationService allocationService;

    public RequisitionRequirementsAllocationController(RequisitionRequirementsAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveAllocation(@RequestBody RequisitionRequirementsAllocationParam param) {

        BaseResponse response = allocationService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<BaseResponse> saveAllAllocation(@RequestBody List <RequisitionRequirementsAllocationParam> params) {

        BaseResponse response = allocationService.saveAll(params);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateAllocation(@PathVariable Long id, @RequestBody RequisitionRequirementsAllocationParam param) {
        BaseResponse response = allocationService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteAllocation(@PathVariable Long id) {
        BaseResponse response = allocationService.deleteById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getAllocationById(@PathVariable Long id) {
        BaseResponse response = allocationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllocationsByMasterId(@PathVariable Long masterId) {
        // Using the user-provided, albeit redundant, service method signature:
        BaseResponse response = allocationService.findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(masterId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllAllocationsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        BaseResponse response = allocationService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllAllocations() {
        BaseResponse response = allocationService.findAll();
        return ResponseEntity.ok(response);
    }
}
