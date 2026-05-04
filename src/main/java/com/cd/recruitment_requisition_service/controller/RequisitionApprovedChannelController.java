package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionApprovedChannelParam;
import com.cd.recruitment_requisition_service.service.RequisitionApprovedChannelService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisition/requisitionApprovedChannel")
@Slf4j
public class RequisitionApprovedChannelController {

    private final RequisitionApprovedChannelService channelService;

    public RequisitionApprovedChannelController(RequisitionApprovedChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse> saveRequisitionApprovedChannel( @RequestBody RequisitionApprovedChannelParam param) {
        log.info("Request to save Requisition Approved Channel for Master ID: {}", param.getRecruitmentRequisitionMasterId());
        BaseResponse response = channelService.save(param);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-all")
    public ResponseEntity<BaseResponse> saveAllRequisitionApprovedChannel( @RequestBody List<RequisitionApprovedChannelParam> params) {
        log.info("Request to save batch Requisition Approved Channel list size: {}", params != null ? params.size() : 0);
        BaseResponse response = channelService.saveAll(params);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateRequisitionApprovedChannel(@PathVariable Long id,@RequestBody RequisitionApprovedChannelParam param) {
        log.info("Request to update Requisition Approved Channel with ID: {}", id);
        BaseResponse response = channelService.update(id, param);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionApprovedChannel(@PathVariable Long id) {
        log.info("Request to soft delete Requisition Approved Channel with ID: {}", id);
        BaseResponse response = channelService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionApprovedChannelById(@PathVariable Long id) {
        log.info("Request to find Requisition Approved Channel by ID: {}", id);
        BaseResponse response = channelService.findById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/master/{masterId}")
    public ResponseEntity<BaseResponse> getAllRequisitionApprovedChannelsByMasterId(@PathVariable Long masterId) {
        log.info("Request to find all Requisition Approved Channels for Master ID: {}", masterId);
        BaseResponse response = channelService.findByRequisitionMasterId(masterId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionApprovedChannelsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        log.info("Request to find all Requisition Approved Channels with pagination (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = channelService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionApprovedChannels() {
        log.info("Request to find all active Requisition Approved Channels");
        BaseResponse response = channelService.findAll();
        return ResponseEntity.ok(response);
    }
}
