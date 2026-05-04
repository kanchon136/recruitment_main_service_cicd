package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseSkillParam;
import com.cd.recruitment_requisition_service.service.RequisitionAreaOfExpertiseSkillService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requisition/requisitionAreaOfExpertiseSkill")
@Slf4j
public class RequisitionAreaOfExpertiseSkillController {
    private final RequisitionAreaOfExpertiseSkillService skillService;

     public RequisitionAreaOfExpertiseSkillController(RequisitionAreaOfExpertiseSkillService skillService) {
        this.skillService = skillService;
    }
    @PostMapping("/area-of-expertise-skill")
    public ResponseEntity<BaseResponse> save(@RequestBody RequisitionAreaOfExpertiseSkillParam param) {
        log.info("Request to save new Requisition Area of Expertise Skill detail.");
        BaseResponse response = skillService.save(param);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/area-of-expertise-skill/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable Long id, @RequestBody RequisitionAreaOfExpertiseSkillParam param) {
        log.info("Request to update Requisition Area of Expertise Skill ID: {}", id);
        BaseResponse response = skillService.update(id, param);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/area-of-expertise-skill/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable Long id) {
        log.info("Request to find Requisition Area of Expertise Skill ID: {}", id);
        BaseResponse response = skillService.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/area-of-expertise-skill/{id}")
    public ResponseEntity<BaseResponse> deleteById(@PathVariable Long id) {
        log.warn("Request to delete Requisition Area of Expertise Skill ID: {}", id);
        BaseResponse response = skillService.deleteById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/area-of-expertise/{parentId}/skills")
    public ResponseEntity<BaseResponse> findAllByParentId(@PathVariable Long parentId) {
        log.info("Request to find all Skills for Parent Area of Expertise ID: {}", parentId);
         BaseResponse response = skillService.findAllByRecordStatusAndRequisitionAreaOfExpertise_idAndRecordStatus(parentId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/area-of-expertise-skill/paged")
    public ResponseEntity<BaseResponse> findAllWithPagination(@RequestParam(defaultValue = "0") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Request to find all Skills (Page: {}, Size: {})", pageNo, pageSize);
        BaseResponse response = skillService.findAllWithPagination(pageNo, pageSize);
        return ResponseEntity.ok(response);
    }
     @GetMapping("/area-of-expertise-skill/all")
    public ResponseEntity<BaseResponse> findAll() {
        log.info("Request to find all Skills.");
        BaseResponse response = skillService.findAll();
        return ResponseEntity.ok(response);
    }
}
