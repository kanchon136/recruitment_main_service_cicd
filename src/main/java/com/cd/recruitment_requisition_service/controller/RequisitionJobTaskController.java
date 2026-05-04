//package com.cd.recruitment_requisition_service.controller;
//
//import com.cd.recruitment_requisition_service.param.RequisitionJobTaskParam;
//import com.cd.recruitment_requisition_service.service.RequisitionJobTaskService;
//import com.cd.recruitment_requisition_service.utils.BaseResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/requisition/requisitionJobTask")
//@Slf4j
//public class RequisitionJobTaskController {
//
//    private final RequisitionJobTaskService jobTaskService;
//
//    // Constructor Injection Pattern
//    public RequisitionJobTaskController(RequisitionJobTaskService jobTaskService) {
//        this.jobTaskService = jobTaskService;
//    }
//
//    @PostMapping("/job-task")
//    public ResponseEntity<BaseResponse> save(@RequestBody RequisitionJobTaskParam param) {
//        log.info("Request to save new Requisition Job Task.");
//        BaseResponse response = jobTaskService.save(param);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/job-task/{id}")
//    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody RequisitionJobTaskParam param) {
//        log.info("Request to update Requisition Job Task ID: {}", id);
//        BaseResponse response = jobTaskService.update(id, param);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/job-task/{id}")
//    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
//        log.info("Request to find Requisition Job Task ID: {}", id);
//        BaseResponse response = jobTaskService.findById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/job-task/{id}")
//    public ResponseEntity<BaseResponse> deleteById(@PathVariable Long id) {
//        log.warn("Request to delete Requisition Job Task ID: {}", id);
//        BaseResponse response = jobTaskService.deleteById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/job-description/{parentId}/job-tasks")
//    public ResponseEntity<BaseResponse> findAllByParentId(@PathVariable Long parentId) {
//        log.info("Request to find all Job Tasks for Parent Job Description ID: {}", parentId);
//        // Note: The method name in the service layer is long, using parentId for clarity here.
//        BaseResponse response = jobTaskService.findAllByRecordStatusAndRequisitionJobDescription_idAndRecordStatus(parentId);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/job-task/paged")
//    public ResponseEntity<BaseResponse> findAllWithPagination(@RequestParam(defaultValue = "0") int pageNo,
//                                                              @RequestParam(defaultValue = "10") int pageSize) {
//        log.info("Request to find all Job Tasks (Page: {}, Size: {})", pageNo, pageSize);
//        BaseResponse response = jobTaskService.findAllWithPagination(pageNo, pageSize);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/job-task/all")
//    public ResponseEntity<BaseResponse> findAll() {
//        log.info("Request to find all Job Tasks.");
//        BaseResponse response = jobTaskService.findAll();
//        return ResponseEntity.ok(response);
//    }
//}
