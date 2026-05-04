package com.cd.recruitment_requisition_service.controller;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.enums.ServiceType;
import com.cd.recruitment_requisition_service.param.RequisitionSupportingDocumentsMasterParam;
import com.cd.recruitment_requisition_service.service.RequisitionSupportingDocumentsMasterService;
import com.cd.recruitment_requisition_service.serviceImpl.StorageService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/requisition/requisitionSupportingDocumentsMaster")
@Slf4j
public class RequisitionSupportingDocumentsMasterController {

    private final RequisitionSupportingDocumentsMasterService masterService;

    public RequisitionSupportingDocumentsMasterController(RequisitionSupportingDocumentsMasterService masterService) {
        this.masterService = masterService;
    }

    @PostMapping(value = "/save", consumes = { "multipart/form-data" })
    public ResponseEntity<BaseResponse> saveRequisitionDocuments(
            @ModelAttribute RequisitionSupportingDocumentsMasterParam param) {

        BaseResponse response = masterService.save(param);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/save-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> saveRequisitionDocuments(
            @RequestPart("RequisitionSupportingDocumentsMasterParam") String requisitionSupportingDocumentsMasterParam,
            @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        RequisitionSupportingDocumentsMasterParam param = objectMapper
                .readValue(requisitionSupportingDocumentsMasterParam, RequisitionSupportingDocumentsMasterParam.class);
        param.setFiles(files);
        BaseResponse response = masterService.save(param);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PostMapping(value = "/update-supporting-documents/{docsMstId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "Update Supporting Documents", description = "Update master info, replace existing files by ID and upload new files")
//    public ResponseEntity<BaseResponse> updateSupportingDocuments(
//
//            @Parameter(description = "Master ID", example = "10") @PathVariable Long docsMstId,
//
//            @Parameter(description = "Supporting document master param as JSON string", example = "{ \"remarks\": \"Updated documents\", \"status\": \"ACTIVE\" }") @RequestPart(value = "supportingDocParam", required = false) String paramJson,
//
//            @Parameter(description = "Existing document IDs (same order as existingFiles)", example = "[\"5\", \"8\"]") @RequestPart(value = "fileIds", required = false) String fileIdsJson,
//
//            @Parameter(description = "Existing files (will replace previous ones)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary"))) @RequestPart(value = "existingFiles", required = false) List<MultipartFile> existingFiles)
//            throws JsonProcessingException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        RequisitionSupportingDocumentsMasterParam param = paramJson != null
//                ? objectMapper.readValue(paramJson, RequisitionSupportingDocumentsMasterParam.class)
//                : new RequisitionSupportingDocumentsMasterParam();
//
//        Map<Long, MultipartFile> existingFileMap = new HashMap<>();
//
//        if (existingFiles != null && fileIdsJson != null) {
//
//            List<String> fileIds = objectMapper.readValue(fileIdsJson, new TypeReference<List<String>>() {
//            });
//
//            if (fileIds.size() != existingFiles.size()) {
//                throw new CustomException("File ID count and existing file count mismatch");
//            }
//
//            for (int i = 0; i < existingFiles.size(); i++) {
//                try {
//                    Long childId = Long.parseLong(fileIds.get(i));
//                    existingFileMap.put(childId, existingFiles.get(i));
//                } catch (NumberFormatException ex) {
//                    throw new CustomException(
//                            "Invalid file ID: " + fileIds.get(i));
//                }
//            }
//        }
//
//        log.info("MasterId = {}", docsMstId);
//        log.info("Param = {}", param);
//        log.info("Existing File Map Keys = {}", existingFileMap.keySet());
//
//        BaseResponse response = masterService.update(docsMstId, param, existingFileMap, null);
//
//        return ResponseEntity.ok(response);
//
//    }


    @PostMapping(value = "/update-supporting-documents/{docsMstId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update Supporting Documents", description = "Update master info, replace existing files by ID and upload new files")
    public ResponseEntity<BaseResponse> updateSupportingDocuments(
            @PathVariable Long docsMstId,
            @RequestPart(value = "supportingDocParam", required = false) String paramJson,
            @RequestPart(value = "fileIds", required = false) String fileIdsJson,
            @RequestPart(value = "existingFiles", required = false) List<MultipartFile> existingFiles,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles) // নতুন ফাইল যোগ করার জন্য
            throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        RequisitionSupportingDocumentsMasterParam param = paramJson != null
                ? objectMapper.readValue(paramJson, RequisitionSupportingDocumentsMasterParam.class)
                : new RequisitionSupportingDocumentsMasterParam();

        // ১. Existing Files হ্যান্ডেল করা (ম্যাপিং আইডি অনুযায়ী)
        Map<Long, MultipartFile> existingFileMap = new HashMap<>();
        if (existingFiles != null && fileIdsJson != null) {
            List<String> fileIds = objectMapper.readValue(fileIdsJson, new TypeReference<List<String>>() {});

            if (fileIds.size() != existingFiles.size()) {
                throw new CustomException("File ID count and existing file count mismatch");
            }

            for (int i = 0; i < existingFiles.size(); i++) {
                Long childId = Long.parseLong(fileIds.get(i));
                existingFileMap.put(childId, existingFiles.get(i));
            }
        }

        log.info("Updating MasterId: {}, New Files Count: {}", docsMstId, (newFiles != null ? newFiles.size() : 0));

        // ২. সার্ভিস কল (existingFileMap এবং newFiles দুটোই পাঠানো হচ্ছে)
        BaseResponse response = masterService.update(docsMstId, param, existingFileMap, newFiles);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<BaseResponse> updateRequisitionDocuments(
            @PathVariable Long id,
            @ModelAttribute RequisitionSupportingDocumentsMasterParam param) {

        BaseResponse response = masterService.update(id, param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getRequisitionDocumentsById(@PathVariable Long id) {
        BaseResponse response = masterService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all/page")
    public ResponseEntity<BaseResponse> getAllRequisitionDocumentsPaginated(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        BaseResponse response = masterService.findAllWithPagination(pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse> getAllRequisitionDocuments() {
        BaseResponse response = masterService.findAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/findAllByRecruitmentRequisitionMasterId/{supperMasterId}")
    public ResponseEntity<BaseResponse> findByRecruitmentRequisitionMasterId(@PathVariable Long supperMasterId) {
        BaseResponse response = masterService.findByRecruitmentRequisitionMasterId(supperMasterId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/findALlByCreateByAndMasterId/{logInUserId}/{masterId}")
    public ResponseEntity<BaseResponse> findALlByRecordStatusAndCreatedBy(@PathVariable String logInUserId,
            @PathVariable Long masterId) {
        BaseResponse response = masterService
                .findAllByRecordStatusAndCreatedByAndRecruitmentRequisitionMaster_idAndRecordStatusAndCreatedBy(
                        masterId, logInUserId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Hidden
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRequisitionDocuments(@PathVariable Long id) {
        BaseResponse response = masterService.deleteById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteByDocsMstIdOrChildIds/{documentMstId}")
    @Operation(summary = "Soft Delete Supporting Document", description = "Soft delete a Requisition Supporting Document master and/or its child files. Provide current user via header. "
            +
            "Optional: send childIds as request param to delete only specific children.")
    public ResponseEntity<BaseResponse> deleteRequisitionDocuments(
            @PathVariable Long documentMstId,
            @RequestParam("currentUserId") String currentUserId,
            @RequestParam(required = false) List<Long> childIds // optional: only delete specific children
    ) {
        BaseResponse response = masterService.deleteById(documentMstId, childIds, currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/loadFileAsResource/{requisitionMasterId}/{filename:.+}")
    public ResponseEntity<?> fileToUri(@PathVariable String requisitionMasterId,
                                       @PathVariable("filename") String filename,
                                       HttpServletRequest request) {

        log.info(">>>> Incoming request to load file: {} for Master ID: {}", filename, requisitionMasterId);

        try {
            // ১. ফাইল লোড করার চেষ্টা করা
            Resource resource = StorageService.loadFileAsResource(requisitionMasterId, filename,
                    ServiceType.RECRUITMENT_REQUISITION_SERVICE);

            // ২. রিসোর্স চেক এবং পাথ ডিবাগিং
            if (resource == null || !resource.exists()) {
                // এই লগটি আপনাকে বলে দিবে অ্যাপ্লিকেশন আসলে কোন ফোল্ডারে ফাইল খুঁজছে
                String attemptedPath = (resource != null) ? resource.getDescription() : "NULL RESOURCE";
                log.error("#### FILE NOT FOUND! Filename: {}, Master ID: {}, Attempted Location: {}",
                        filename, requisitionMasterId, attemptedPath);
                return ResponseEntity.notFound().build();
            }

            log.info("Successfully found file: {} at URI: {}", filename, resource.getURI());

            // ৩. কনটেন্ট টাইপ বা মাইম টাইপ ডিটেকশন
            String contentType = null;
            try {
                // লিনাক্সে resource.getURL().getPath() অনেক সময় বেশি কার্যকর
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

                if (contentType == null) {
                    contentType = Files.probeContentType(Paths.get(resource.getURI()));
                }
            } catch (Exception ex) {
                log.warn("Could not determine file type for {}, Error: {}", filename, ex.getMessage());
                contentType = "application/octet-stream";
            }

            log.info("Serving file: {} with Content-Type: {}", filename, contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("XXXX Serious Error occurred while serving file: {} - Message: {}", filename, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error occurred: " + e.getMessage());
        }
    }

}
