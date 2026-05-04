package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.service.RequisitionModuleCountService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requisition/requisitionModuleCount")
@Slf4j
public class RequisitionModuleCountController {

    private final RequisitionModuleCountService moduleCountService;

    public RequisitionModuleCountController(RequisitionModuleCountService moduleCountService) {
        this.moduleCountService = moduleCountService;
    }

    @GetMapping("/getRequisitionModuleCountData/{parentId}")
    public ResponseEntity<BaseResponse> getRequisitionModuleCountData(@PathVariable("parentId") Long parenTRequisitionId) {

        BaseResponse response = moduleCountService.findALlRequisitionModuleCountData(parenTRequisitionId);
        return ResponseEntity.ok(response);
    }
}
