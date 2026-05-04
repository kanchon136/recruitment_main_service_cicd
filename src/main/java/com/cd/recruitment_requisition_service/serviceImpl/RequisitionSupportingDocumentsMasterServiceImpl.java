package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionSupportingDocumentsMasterDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionSupportingDocumentsChild;
import com.cd.recruitment_requisition_service.entity.RequisitionSupportingDocumentsMaster;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.enums.ServiceType;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
 import com.cd.recruitment_requisition_service.param.RequisitionSupportingDocumentsMasterParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionSupportingDocumentsChildRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionSupportingDocumentsMasterRepository;
import com.cd.recruitment_requisition_service.service.FileStorageService;
import com.cd.recruitment_requisition_service.service.RequisitionSupportingDocumentsMasterService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
 import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class RequisitionSupportingDocumentsMasterServiceImpl implements RequisitionSupportingDocumentsMasterService {

    private static final String DOWNLOAD_BASE_URL = "/api/recruitment/requisition/document/download/";

    private final RequisitionSupportingDocumentsMasterRepository masterRepository;
    private final RecruitmentRequisitionMasterRepository requisitionMasterRepository;
    private final FileStorageService fileStorageService;
    private final RequisitionSupportingDocumentsChildRepository childRepository;

    public RequisitionSupportingDocumentsMasterServiceImpl(RequisitionSupportingDocumentsMasterRepository masterRepository,
                                                           RecruitmentRequisitionMasterRepository requisitionMasterRepository,
                                                           FileStorageService fileStorageService,
                                                           RequisitionSupportingDocumentsChildRepository childRepository ) {
        this.masterRepository = masterRepository;
        this.requisitionMasterRepository = requisitionMasterRepository;
        this.fileStorageService = fileStorageService;
        this.childRepository = childRepository;
    }

     private RequisitionSupportingDocumentsMasterDto convertToDtoWithDownloadLink(RequisitionSupportingDocumentsMaster masterEntity) {
        // This relies on EntityAndDtoAllMapper to handle child entities and their download URL links

        RequisitionSupportingDocumentsMasterDto dto = EntityAndDtoAllMapper.masterEntityToDto(masterEntity);
        return dto;
    }

    @Override
    @Transactional
    public BaseResponse save(RequisitionSupportingDocumentsMasterParam param) {
        BaseResponse response = new BaseResponse();

         RecruitmentRequisitionMaster parent = requisitionMasterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Active Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        RequisitionSupportingDocumentsMaster masterEntity = new RequisitionSupportingDocumentsMaster();
        BeanUtils.copyProperties(param, masterEntity, "files");
        masterEntity.setRecruitmentRequisitionMaster(parent);

         masterEntity.setRecordStatus(RecordStatus.ACTIVE);
         masterEntity.setCreatedBy(param.getActedUserCode()); // Commented as requested
         masterEntity.setActedUserName(param.getActedUserName());
        // masterEntity.setUpdatedBy(currentUserId); // Commented as requested
        masterEntity.setCreatedDate(LocalDate.now());
        masterEntity.setCreatedDateTime(LocalDateTime.now());

        RequisitionSupportingDocumentsMaster savedEntity = masterRepository.save(masterEntity);
        Long generatedMasterId = savedEntity.getId();

         if (param.getFiles() != null && !param.getFiles().isEmpty()) {

            List<RequisitionSupportingDocumentsChild> childEntities = param.getFiles().stream()
                    .filter(fileData -> fileData != null && !fileData.isEmpty())
                    .map(fileData -> {

                        String subFolder = "RECRUITMENT_REQUISITION"+ File.separator + generatedMasterId;

                        String fileName = StorageService.copyFile(fileData, subFolder);

                        log.info("fileName====>"+fileName);

                        RequisitionSupportingDocumentsChild child = new RequisitionSupportingDocumentsChild();

                        child.setFileName(fileName);

                        child.setFileSize(fileData.getSize());
                        log.info("fileSix");
                        child.setContentType(fileData.getContentType());

                        child.setRequisitionSupportingDocumentsMaster(savedEntity); // Link to the entity with ID
                        child.setRecordStatus(RecordStatus.ACTIVE);
                        child.setCreatedBy(param.getActedUserCode()); // Commented as requested
                        child.setActedUserName(param.getActedUserName());
                        child.setUploadDateTime(LocalDateTime.now());
                         return child;
                    })
                    .collect(Collectors.toList());

             savedEntity.setFiles(childEntities);
        } else {
            savedEntity.setFiles(new ArrayList<>());
        }

        RequisitionSupportingDocumentsMasterDto dto = this.convertToDtoWithDownloadLink(savedEntity);
         response.setRequisitionSupportingDocumentsMasterDto(dto);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse update(Long id, RequisitionSupportingDocumentsMasterParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionSupportingDocumentsMaster existing = masterRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Supporting Documents Master not found for ID: " + id));

        Long masterId = existing.getRecruitmentRequisitionMaster().getId();

        copyNonNullProperties(param, existing); // Update master-level fields

         if (param.getFiles() != null && !param.getFiles().isEmpty()) {

             existing.getFiles().forEach(child -> fileStorageService.deleteFile(child.getFilePath()));
             existing.getFiles().clear();

             List<RequisitionSupportingDocumentsChild> newChildEntities = param.getFiles().stream()
                    .filter(fileData -> fileData != null && !fileData.isEmpty())
                    .map(fileData -> {
                        String filePath = fileStorageService.storeFile(fileData, masterId);

                        RequisitionSupportingDocumentsChild child = new RequisitionSupportingDocumentsChild();

                        child.setFileName(fileData.getOriginalFilename());
                        child.setFilePath(filePath);
                        child.setFileSize(fileData.getSize());
                        child.setContentType(fileData.getContentType());

                        child.setRequisitionSupportingDocumentsMaster(existing);
                        child.setRecordStatus(RecordStatus.ACTIVE);
                       // child.setCreatedBy(existing.getCreatedBy() != null ? existing.getCreatedBy() : currentUserId);
                         child.setUpdatedBy(param.getUpdatedBy());
                        return child;
                    })
                    .collect(Collectors.toList());

             existing.getFiles().addAll(newChildEntities);
        }

        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        RequisitionSupportingDocumentsMaster updated = masterRepository.save(existing);

        RequisitionSupportingDocumentsMasterDto dto = this.convertToDtoWithDownloadLink(updated);
         response.setRequisitionSupportingDocumentsMasterDto(dto);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Transactional
    @Override
    public BaseResponse deleteById(Long id) {

        BaseResponse response = new BaseResponse();

        RequisitionSupportingDocumentsMaster existing =
                masterRepository
                        .findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                        .orElseThrow(() ->
                                new CustomException(
                                        "Requisition Supporting Documents Master not found for ID: " + id));

        // 1️ Child soft delete
        existing.getFiles().forEach(child -> {
            child.setRecordStatus(RecordStatus.DELETED);
        });

        // 2️ Master soft delete
        existing.setRecordStatus(RecordStatus.DELETED);

        masterRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    @Transactional
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RequisitionSupportingDocumentsMaster> optional = masterRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Requisition Supporting Documents Master not found for ID: " + id);
            return response;
        }

        RequisitionSupportingDocumentsMasterDto dto = this.convertToDtoWithDownloadLink(optional.get());
        response.setRequisitionSupportingDocumentsMasterDto(dto);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {

        BaseResponse response = new BaseResponse();
         PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionSupportingDocumentsMaster> entityPage = masterRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        List<RequisitionSupportingDocumentsMasterDto> dtoList = entityPage.getContent().stream()
                .map(this::convertToDtoWithDownloadLink)
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionSupportingDocumentsMaster> entityList = masterRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionSupportingDocumentsMasterDto> dtoList = entityList.stream()
                .map(this::convertToDtoWithDownloadLink)
                .collect(Collectors.toList());

         response.setRequisitionSupportingDocumentsMasterDtos(dtoList); // Assuming BaseResponse has a field to hold a list
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

     private void copyNonNullProperties(RequisitionSupportingDocumentsMasterParam source, RequisitionSupportingDocumentsMaster target) {
        if (source.getDocumentTitle() != null) target.setDocumentTitle(source.getDocumentTitle());
     }

    @Override
    public BaseResponse findByRecruitmentRequisitionMasterId(Long masterId) {

        BaseResponse response = new BaseResponse();

        List<RequisitionSupportingDocumentsMaster> entityList =
                masterRepository
                        .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(
                                RecordStatus.ACTIVE, masterId)
                        .stream()
                        .peek(master -> {
                            List<RequisitionSupportingDocumentsChild> activeFiles =
                                    master.getFiles()
                                            .stream()
                                            .filter(file -> file.getRecordStatus() == RecordStatus.ACTIVE)
                                            .collect(Collectors.toList());

                            master.setFiles(activeFiles);
                        })
                        .toList();

        List<RequisitionSupportingDocumentsMasterDto> dtoList =
                entityList.stream()
                        .map(this::convertToDtoWithDownloadLink)
                        .collect(Collectors.toList());

        response.setRequisitionSupportingDocumentsMasterDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());

        return response;
    }

    @Override
    public BaseResponse findALlByRecordStatusAndCreatedBy(String logInUserId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionSupportingDocumentsMaster> entityList =
                masterRepository
                        .findALlByRecordStatusAndCreatedBy(RecordStatus.ACTIVE, logInUserId)
                        .stream()
                        .peek(master -> {
                            if (master.getFiles() != null) {
                                master.setFiles(
                                        master.getFiles().stream()
                                                .filter(f ->
                                                        f.getCreatedBy() != null &&
                                                                master.getCreatedBy() != null &&
                                                                f.getCreatedBy().equalsIgnoreCase(master.getCreatedBy())
                                                ).collect(Collectors.toList()) );
                            }
                        }).toList();


        List<RequisitionSupportingDocumentsMasterDto> dtoList = entityList.stream()
                .map(this::convertToDtoWithDownloadLink)
                .collect(Collectors.toList());

        response.setRequisitionSupportingDocumentsMasterDtos(dtoList); // Assuming BaseResponse has a field to hold a list
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
    return  response;
    }

    @Override
    public BaseResponse findAllByRecordStatusAndCreatedByAndRecruitmentRequisitionMaster_idAndRecordStatusAndCreatedBy(Long masterId, String logInUserId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionSupportingDocumentsMaster> entityList =
                masterRepository
                        .findAllByRecordStatusAndCreatedByAndRecruitmentRequisitionMaster_idAndRecordStatusAndCreatedBy(
                                RecordStatus.ACTIVE, logInUserId,masterId,RecordStatus.ACTIVE,logInUserId)
                        .stream()
                        .peek(master -> {
                            if (master.getFiles() != null) {
                                master.setFiles(
                                        master.getFiles().stream()
                                                .filter(f ->
                                                        f.getRecordStatus() == RecordStatus.ACTIVE &&
                                                        f.getCreatedBy() != null &&
                                                                master.getCreatedBy() != null &&
                                                                f.getCreatedBy().equalsIgnoreCase(master.getCreatedBy())
                                                ).collect(Collectors.toList()) );
                            }
                        }).toList();


        List<RequisitionSupportingDocumentsMasterDto> dtoList = entityList.stream()
                .map(this::convertToDtoWithDownloadLink)
                .collect(Collectors.toList());

        response.setRequisitionSupportingDocumentsMasterDtos(dtoList); // Assuming BaseResponse has a field to hold a list
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return  response;

    }

//    @Override
//    public BaseResponse update(Long masterId, RequisitionSupportingDocumentsMasterParam param,
//                               Map<Long, MultipartFile> existingFiles, List<MultipartFile> newFiles) {
//
//        RequisitionSupportingDocumentsMaster master =
//                masterRepository.findById(masterId)
//                        .orElseThrow(() -> new CustomException("Master not found"));
//
//        // ---- UPDATE MASTER BASIC FIELDS ----
//        BeanUtils.copyProperties(param, master, "id", "files");
//
//        String baseFolder =
//                "RECRUITMENT_REQUISITION" + File.separator + masterId;
//
//    /* =====================================================
//       UPDATE EXISTING FILES ONLY (NO NEW FILE)
//       ===================================================== */
//        if (existingFiles != null && !existingFiles.isEmpty()) {
//
//            existingFiles.forEach((childId, file) -> {
//
//                if (file == null || file.isEmpty()) {
//                    return;
//                }
//
//                RequisitionSupportingDocumentsChild child =
//                        childRepository.findByIdAndRecordStatus(childId, RecordStatus.ACTIVE)
//                                .orElseThrow(() ->
//                                        new CustomException("File not found for ID: " + childId));
//
//                // overwrite same folder
//                String fileName = StorageService.copyFile(file, baseFolder);
//
//                child.setFileName(fileName);
//                child.setFileSize(file.getSize());
//                child.setContentType(file.getContentType());
//                child.setUpdatedBy(param.getActedUserCode());
//                child.setUploadDateTime(LocalDateTime.now());
//            });
//        }
//
//        masterRepository.save(master);
//
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.SUCCESS.getStatus());
//        response.setRequisitionSupportingDocumentsMasterDto(
//                convertToDtoWithDownloadLink(master)
//        );
//
//        return response;
//    }
@Override
@Transactional
public BaseResponse update(Long masterId, RequisitionSupportingDocumentsMasterParam param,
                           Map<Long, MultipartFile> existingFiles, List<MultipartFile> newFiles) {

    RequisitionSupportingDocumentsMaster master = masterRepository.findById(masterId)
            .orElseThrow(() -> new CustomException("Master not found for ID: " + masterId));

    // ১. আপডেট মাস্টার বেসিক ফিল্ডস (যেমন: remarks, status ইত্যাদি)
    BeanUtils.copyProperties(param, master, "id", "files");
    master.setUpdatedBy(param.getActedUserCode());
    master.setUpdatedAt(LocalDateTime.now());
   // master.setUpdatedDateTime(LocalDateTime.now());

    String subFolder = "RECRUITMENT_REQUISITION" + File.separator + masterId;

    /* =====================================================
       ২. EXISTING FILES আপডেট করা (ID ধরে ধরে)
       ===================================================== */
    if (existingFiles != null && !existingFiles.isEmpty()) {
        existingFiles.forEach((childId, fileData) -> {
            if (fileData != null && !fileData.isEmpty()) {
                RequisitionSupportingDocumentsChild child = childRepository
                        .findByIdAndRecordStatus(childId, RecordStatus.ACTIVE)
                        .orElseThrow(() -> new CustomException("Active Child File not found for ID: " + childId));

                // পুরাতন ফাইল রিপ্লেস বা ওভাররাইট করা
                String fileName = StorageService.copyFile(fileData, subFolder);

                child.setFileName(fileName);
                child.setFileSize(fileData.getSize());
                child.setContentType(fileData.getContentType());
                child.setUpdatedBy(param.getActedUserCode());
                child.setUploadDateTime(LocalDateTime.now());
                // এখানে সরাসরি সেভ করার দরকার নেই, মাস্টার সেভ হলে এটিও হবে
            }
        });
    }

    /* =====================================================
       ৩. NEW FILES অ্যাড করা (একদম নতুন রেকর্ড)
       ===================================================== */
    if (newFiles != null && !newFiles.isEmpty()) {
        List<RequisitionSupportingDocumentsChild> newChildEntities = newFiles.stream()
                .filter(fileData -> fileData != null && !fileData.isEmpty())
                .map(fileData -> {
                    String fileName = StorageService.copyFile(fileData, subFolder);

                    RequisitionSupportingDocumentsChild child = new RequisitionSupportingDocumentsChild();
                    child.setFileName(fileName);
                    child.setFileSize(fileData.getSize());
                    child.setContentType(fileData.getContentType());

                    child.setRequisitionSupportingDocumentsMaster(master); // লিঙ্কিং
                    child.setRecordStatus(RecordStatus.ACTIVE);
                    child.setCreatedBy(param.getActedUserCode());
                    child.setActedUserName(param.getActedUserName());
                    child.setUploadDateTime(LocalDateTime.now());
                    return child;
                })
                .collect(Collectors.toList());

        // বর্তমান লিস্টের সাথে নতুনগুলো যোগ করা
        if (master.getFiles() == null) {
            master.setFiles(new ArrayList<>());
        }
        master.getFiles().addAll(newChildEntities);
    }

    // চূড়ান্ত সেভ
    RequisitionSupportingDocumentsMaster savedMaster = masterRepository.save(master);

    BaseResponse response = new BaseResponse();
    response.setMessage(ResponseEnum.SUCCESS.getStatus());
    response.setRequisitionSupportingDocumentsMasterDto(
            this.convertToDtoWithDownloadLink(savedMaster)
    );

    return response;
}

    @Override
    @Transactional
    public BaseResponse deleteById(Long docsMstId, List<Long> childIds, String currentUserId) {

        BaseResponse response = new BaseResponse();

        RequisitionSupportingDocumentsMaster master =
                masterRepository.findByIdAndRecordStatus(docsMstId, RecordStatus.ACTIVE)
                        .orElseThrow(() ->
                                new CustomException(
                                        "Supporting Documents Master not found for ID: " + docsMstId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        if (master.getFiles() == null || master.getFiles().isEmpty()) {
            throw new CustomException("No child files found under this master");
        }

        if (childIds == null || childIds.isEmpty()) {

            //  Soft delete all children
            master.getFiles().forEach(child -> {
                child.setRecordStatus(RecordStatus.DELETED);
                child.setUpdatedBy(currentUserId);
                child.setUpdatedAt(now);
            });

            //  Soft delete master
            master.setRecordStatus(RecordStatus.DELETED);
            master.setUpdatedBy(currentUserId);
            master.setUpdatedAt(now);

            response.setMessage("Master and all children deleted successfully");

        } else {

            //  Soft delete only selected children
            master.getFiles().forEach(child -> {
                if (childIds.contains(child.getId())
                        && child.getRecordStatus() == RecordStatus.ACTIVE) {

                    child.setRecordStatus(RecordStatus.DELETED);
                    child.setUpdatedBy(currentUserId);
                    child.setUpdatedAt(now);
                }
            });

            //  Auto delete master if all children are deleted
            boolean allDeleted = master.getFiles()
                    .stream()
                    .allMatch(child -> child.getRecordStatus() == RecordStatus.DELETED);

            if (allDeleted) {
                master.setRecordStatus(RecordStatus.DELETED);
                master.setUpdatedBy(currentUserId);
                master.setUpdatedAt(now);
            }

            response.setMessage("Selected children deleted successfully");
        }

        // ⚡ Only master save
        masterRepository.save(master);

        return response;
    }





}
