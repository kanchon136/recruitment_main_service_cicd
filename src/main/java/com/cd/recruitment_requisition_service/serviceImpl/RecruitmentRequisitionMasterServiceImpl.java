package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RecruitmentRequisitionMasterDto;
import com.cd.recruitment_requisition_service.dto.RequisitionApprovalCompletionStatusDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.enums.*;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RecruitmentRequisitionMasterParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionReplacementPersonRepository;
import com.cd.recruitment_requisition_service.service.RecruitmentRequisitionMasterService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.cd.recruitment_requisition_service.utils.DynamicCodeGenerator;
import com.cd.recruitment_requisition_service.utils.PaginatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecruitmentRequisitionMasterServiceImpl implements RecruitmentRequisitionMasterService {

    private final RecruitmentRequisitionMasterRepository masterRepo;
    private final DynamicCodeGenerator dynamicCodeGenerator;
    private final RequisitionReplacementPersonRepository replacementPersonRepository;


    public RecruitmentRequisitionMasterServiceImpl(RecruitmentRequisitionMasterRepository masterRepo,
                                                   DynamicCodeGenerator dynamicCodeGenerator,
                                                   RequisitionReplacementPersonRepository replacementPersonRepository) {
        this.masterRepo = masterRepo;
        this.dynamicCodeGenerator = dynamicCodeGenerator;
        this.replacementPersonRepository = replacementPersonRepository;
    }


    @Override
    public BaseResponse save(RecruitmentRequisitionMasterParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster entity = new RecruitmentRequisitionMaster();
        BeanUtils.copyProperties(param, entity);


        String finalRequisitionCode = generateOrUpdateRequisitionCode(entity,param);

        entity.setRequisitionCode(finalRequisitionCode);

        entity.setCurrentStatus(RequisitionStatus.DRAFT);
        entity.setOverallProcessStatus(OverallProcessStatus.DRAFT);
        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setCurrentLayerPosition(0);
        entity.setCurrentRole(String.valueOf(AuthorizationType.INITIATOR)); // was RAISER
        entity.setCreatedBy(param.getActedUserCode());
        entity.setActedUserName(param.getActedUserName());

        RecruitmentRequisitionMaster savedEntity = masterRepo.save(entity);
        response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());

        return response;
    }

    @Override
    public BaseResponse update(Long id, RecruitmentRequisitionMasterParam param) {
        BaseResponse response = new BaseResponse();
        //  String currentUserId = UserContext.getCurrentUserId();

        Optional<RecruitmentRequisitionMaster> optional = masterRepo.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Recruitment Requisition not found for ID: " + id);
            return response;
        }

        RecruitmentRequisitionMaster existing = optional.get();

//        if (existing.getCurrentStatus() != RequisitionStatus.DRAFT) {
//            throw new CustomException("Cannot update requisition. Status must be DRAFT or Returned to Requester.");
//        }

        copyNonNullProperties(param, existing);
        String requisitionCode = generateOrUpdateRequisitionCode(existing,param);

        existing.setRequisitionCode(requisitionCode);
        existing.setUpdatedBy(param.getUpdatedBy());
        existing.setUpdatedAt(LocalDateTime.now());
        RecruitmentRequisitionMaster updated = masterRepo.save(existing);

        RecruitmentRequisitionMasterDto dto = EntityAndDtoAllMapper.requisitionEntityToDto(updated);
        response.setRecruitmentRequisitionMasterDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

     private String generateOrUpdateRequisitionCode(RecruitmentRequisitionMaster entity, RecruitmentRequisitionMasterParam param) {
        LocalDate now = LocalDate.now();

        // ১. Prefix 'R' for requisition
        String prefix = "R";

        // ২. Personal Area (Spaces রিমুভ করে)
       // String pArea = (param.getPersonalAreaName() != null) ? param.getPersonalAreaName().replaceAll("\\s+", "") : "";
        String pArea = (param.getPersonalAreaName() != null) ? param.getPersonalAreaName() : "";

        // ৩. Management Type (M/N) - Category অনুযায়ী
        String mgmtType = (param.getEmployeeCategoryName() != null && param.getEmployeeCategoryName().equalsIgnoreCase("Non-Management")) ? "N" : "M";

        // ৪. Requisition Type (NEW -> N, REPLACEMENT -> R)
        String typeCode = (param.getRequisitionType() == RequisitionType.REPLACE) ? "R" : "N";

        log.info("typeCode====>"+typeCode);

        // ৫. তারিখ (ddMMMyy)
        String dateStr = now.format(DateTimeFormatter.ofPattern("ddMMMyy"));

        String seqNumber;

        // ৬. সিকোয়েন্স লজিক (Monthly Reset 01-99)
        if (entity.getId() == null) {
            // CREATE: ডাটাবেজ কাউন্ট + ১
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

//            long currentMonthCount = masterRepo.countByCreatedDateBetweenAndRecordStatus(
//                    startOfMonth, endOfMonth, RecordStatus.ACTIVE);

            long currentMonthCount = masterRepo.countByCreatedDateBetween(
                    startOfMonth, endOfMonth);

            if (currentMonthCount >= 99) {
                throw new CustomException("Monthly limit reached (99).");
            }
            seqNumber = String.format("%02d", currentMonthCount + 1);
        } else {
            // UPDATE: বর্তমান কোডের শেষ ২ ডিজিট ধরে রাখবে
            if (entity.getRequisitionCode() != null && entity.getRequisitionCode().contains("_")) {
                String[] parts = entity.getRequisitionCode().split("_");
                seqNumber = parts[parts.length - 1];
            } else {
                seqNumber = "01";
            }
        }

        // ফরম্যাট: R_Dhaka_M_N_02Feb26_01
        return String.format("%s_%s_%s_%s_%s_%s", prefix, pArea, mgmtType, typeCode, dateStr, seqNumber);
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RecruitmentRequisitionMaster> optional = masterRepo.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Recruitment Requisition not found for ID: " + id);
            return response;
        }

        RecruitmentRequisitionMaster existing = optional.get();

        if (existing.getCurrentStatus() != RequisitionStatus.DRAFT
             //   && existing.getCurrentStatus() != RequisitionStatus.WITHDRAWN && existing.getCurrentStatus() != RequisitionStatus.REJECTED
        ) {
            throw new CustomException("Cannot delete requisition. Only DRAFT statuses are deletable.");
        }

        existing.setRecordStatus(RecordStatus.DELETED);
        //  existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        masterRepo.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        Optional<RecruitmentRequisitionMaster> optional = masterRepo.findByIdAndRecordStatus(id, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Recruitment Requisition not found for ID: " + id);
            return response;
        }

        RecruitmentRequisitionMasterDto dto = EntityAndDtoAllMapper.requisitionEntityToDto(optional.get());
        response.setRecruitmentRequisitionMasterDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdDateTime").descending());
        Page<RecruitmentRequisitionMaster> page = masterRepo.findAll(pageRequest);

        PaginatedResponse<RecruitmentRequisitionMasterDto> pagination =
                PaginatedResponse.fromPage(page.map(EntityAndDtoAllMapper::requisitionEntityToDto));

        BaseResponse response = new BaseResponse();
        response.setRecruitmentRequisitionMasterDtoPaginatedResponse(pagination); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        var list = masterRepo.findAllByRecordStatus(RecordStatus.ACTIVE);
        BaseResponse response = new BaseResponse();
        response.setRecruitmentRequisitionMasterDtos(list.stream() // Placeholder DTO setter
                .map(EntityAndDtoAllMapper::requisitionEntityToDto)
                .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse submitRequisition(Long requisitionId, String raiserId) {
        // RecruitmentRequisitionMaster master = approvalHandler.submitRequisition(requisitionId, raiserId);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse approveRequisition(Long channelId, String approverId, String comments) {
        //   RecruitmentRequisitionMaster master = approvalHandler.handleGeneralApprovalAction(channelId, ApprovalAction.APPROVE, approverId, comments);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse rejectRequisition(Long channelId, String approverId, String comments) {
        //  RecruitmentRequisitionMaster master = approvalHandler.handleGeneralApprovalAction(channelId, ApprovalAction.REJECT, approverId, comments);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse sendBackRequisition(Long channelId, String approverId, String comments) {
        // RecruitmentRequisitionMaster master = approvalHandler.handleGeneralApprovalAction(channelId, ApprovalAction.RETURN_TO_REQUESTER, approverId, comments);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse postponeRequisition(Long requisitionId, String actingUserId, String justification) {
        //  RecruitmentRequisitionMaster master = approvalHandler.postponeRequisition(requisitionId, actingUserId, justification);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse withdrawRequisition(Long requisitionId, String actingUserId, String justification) {
        //  RecruitmentRequisitionMaster master = approvalHandler.withdrawRequisition(requisitionId, actingUserId, justification);
        BaseResponse response = new BaseResponse();
        // response.setRecruitmentRequisitionMasterDto(EntityAndDtoAllMapper.requisitionEntityToDto(master));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse createRequisitionFromPlan(Long manpowerPlanningMasterId, String raiserId) {
        BaseResponse response = new BaseResponse();
        // Placeholder: Call to External Planning Service to fetch and copy data
        log.info("Attempting to create Requisition from Manpower Plan ID: {}", manpowerPlanningMasterId);

        // 1. Fetch data from Planning Service
        // ManpowerPlanningMasterData planningData = planningService.getManpowerPlanData(manpowerPlanningMasterId);
        // if (planningData == null) {
        //     throw new CustomException("Manpower Planning Master not found or no active data.");
        // }

        // 2. Create and map the new Requisition Entity
        RecruitmentRequisitionMaster entity = new RecruitmentRequisitionMaster();
        //  BeanUtils.copyProperties(planningData, entity); // Copy fields from Planning Data DTO

        // Set mandatory fields from Plan
        //entity.setManpowerPlanningMasterId(manpowerPlanningMasterId);
        entity.setIsPlanBased(true);
        // entity.setRequisitionType(RequisitionType.PLAN_BASED);
        // entity.setHiringManagerId(planningData.getHiringManagerId()); // Example of a required field

        entity.setRequisitionCode(dynamicCodeGenerator.generateCode("rr_sequence", "RR", "ddMMyyyy"));
        entity.setCurrentStatus(RequisitionStatus.DRAFT);
        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedBy(raiserId);
        entity.setUpdatedBy(raiserId);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());

        RecruitmentRequisitionMaster savedEntity = masterRepo.save(entity);

        // 4. Copy Detail Entities (Job Description, etc.) - This typically happens in the service layer or a dedicated handler
        // planningService.copyDetailEntities(manpowerPlanningMasterId, savedEntity.getId());

        RecruitmentRequisitionMasterDto dto = EntityAndDtoAllMapper.requisitionEntityToDto(savedEntity);
        // response.setRecruitmentRequisitionMasterDto(dto);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse checkApprovalCompletion(Long masterRequisitionId) {
        BaseResponse baseResponse = new BaseResponse();

        RecruitmentRequisitionMaster requisition = masterRepo.findByIdAndRecordStatus(masterRequisitionId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Requisition not found for ID: " + masterRequisitionId));

        RequisitionStatus currentStatus = requisition.getCurrentStatus();

        // ... (Logic for isApproved and isTerminal remains the same) ...
        boolean isApproved = currentStatus == RequisitionStatus.FINAL_APPROVED;
        boolean isTerminal = isApproved ||
                currentStatus == RequisitionStatus.REJECTED ||
                currentStatus == RequisitionStatus.WITHDRAWN;

        // 3. Prepare DTO using the new class name
        RequisitionApprovalCompletionStatusDto dto = RequisitionApprovalCompletionStatusDto.builder()
                .masterRequisitionId(masterRequisitionId)
                .requisitionCode(requisition.getRequisitionCode())
                .isApproved(isApproved)
                .isTerminalStatus(isTerminal)
                .finalStatus(currentStatus)
                .build();

        // 4. Set DTO in BaseResponse
        // NOTE: BaseResponse-এ এই সেটারটি যোগ করতে হবে
        baseResponse.setRequisitionApprovalCompletionStatusDto(dto);
        baseResponse.setMessage(ResponseEnum.SUCCESS.getStatus());

        log.info("Completion status checked for Requisition {}: Status={}, Is Approved={}",
                requisition.getRequisitionCode(), currentStatus, isApproved);

        return baseResponse;
    }

    @Override
    public BaseResponse findByRequisitionMaterIdWithAllReference(Long requisitionMasterId) {

        BaseResponse response = new BaseResponse();
        Optional<RecruitmentRequisitionMaster> optional = masterRepo.findByIdAndRecordStatus(requisitionMasterId, RecordStatus.ACTIVE);
        if (optional.isEmpty()) {
            response.setMessage("Recruitment Requisition not found for ID: " + requisitionMasterId);
            return response;
        }
       // RecruitmentRequisitionMasterDto dto = EntityAndDtoAllMapper.requisitionMasterEntityToDtoWithAllReferenceDto(optional.get());
        RecruitmentRequisitionMasterDto dto = EntityAndDtoAllMapper.requisitionMasterEntityToDtoWithAllReferenceDto(optional.get(),replacementPersonRepository);
        response.setRecruitmentRequisitionMasterDto(dto); // Placeholder DTO setter
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByRecordStatusAndCurrentStatusAndUserId(String userId) {
        var list = masterRepo.findAllByRecordStatusAndCurrentStatusAndUserId(RecordStatus.ACTIVE, RequisitionStatus.DRAFT, userId);
        BaseResponse response = new BaseResponse();
        response.setRecruitmentRequisitionMasterDtos(list.stream() // Placeholder DTO setter
                .map(EntityAndDtoAllMapper::requisitionEntityToDto)
                .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RecruitmentRequisitionMasterParam source, RecruitmentRequisitionMaster target) {
        if (source == null || target == null) return;

        // --- Basic Information ---
        if (source.getUserId() != null && !source.getUserId().isBlank()) target.setUserId(source.getUserId());
        if (source.getUserName() != null && !source.getUserName().isBlank()) target.setUserName(source.getUserName());
        if (source.getIsPlanBased() != null) target.setIsPlanBased(source.getIsPlanBased());
        if (source.getRequisitionType() != null) target.setRequisitionType(source.getRequisitionType());
        if (source.getProcessInitiationDate() != null) target.setProcessInitiationDate(source.getProcessInitiationDate());

        // --- Position & Requisition Details ---
        if (source.getPositionName() != null && !source.getPositionName().isBlank()) target.setPositionName(source.getPositionName());
        if (source.getPositionCode() != null && !source.getPositionCode().isBlank()) target.setPositionCode(source.getPositionCode());
        if (source.getDemandInRequisition() != null) target.setDemandInRequisition(source.getDemandInRequisition());
        if (source.getTargetHireDate() != null) target.setTargetHireDate(source.getTargetHireDate());
        if (source.getSalaryRangeFrom() != null) target.setSalaryRangeFrom(source.getSalaryRangeFrom());
        if (source.getSalaryRangeTo() != null) target.setSalaryRangeTo(source.getSalaryRangeTo());

        // --- Organizational Hierarchy (Missing Properties) ---
        if (source.getCompanyCode() != null && !source.getCompanyCode().isBlank()) target.setCompanyCode(source.getCompanyCode());
        if (source.getCompanyName() != null && !source.getCompanyName().isBlank()) target.setCompanyName(source.getCompanyName());
        if (source.getEmployeeCategoryCode() != null && !source.getEmployeeCategoryCode().isBlank()) target.setEmployeeCategoryCode(source.getEmployeeCategoryCode());
        if (source.getEmployeeCategoryName() != null && !source.getEmployeeCategoryName().isBlank()) target.setEmployeeCategoryName(source.getEmployeeCategoryName());
        if (source.getEmployeeSubGroupCode() != null && !source.getEmployeeSubGroupCode().isBlank()) target.setEmployeeSubGroupCode(source.getEmployeeSubGroupCode());
        if (source.getEmployeeSubGroupName() != null && !source.getEmployeeSubGroupName().isBlank()) target.setEmployeeSubGroupName(source.getEmployeeSubGroupName());
        if (source.getPersonalAreaCode() != null && !source.getPersonalAreaCode().isBlank()) target.setPersonalAreaCode(source.getPersonalAreaCode());
        if (source.getPersonalAreaName() != null && !source.getPersonalAreaName().isBlank()) target.setPersonalAreaName(source.getPersonalAreaName());
        if (source.getWorkplaceCode() != null && !source.getWorkplaceCode().isBlank()) target.setWorkplaceCode(source.getWorkplaceCode());
        if (source.getWorkplaceName() != null && !source.getWorkplaceName().isBlank()) target.setWorkplaceName(source.getWorkplaceName());

        // --- Approval & Tracking Fields ---
        if (source.getCurrentStatus() != null) target.setCurrentStatus(source.getCurrentStatus());
        if (source.getCurrentRole() != null && !source.getCurrentRole().isBlank()) target.setCurrentRole(source.getCurrentRole());
        if (source.getNextRole() != null && !source.getNextRole().isBlank()) target.setNextRole(source.getNextRole());
        if (source.getBusinessUnit() != null && !source.getBusinessUnit().isBlank()) target.setBusinessUnit(source.getBusinessUnit());
        if (source.getCurrentLayerPosition() != null) target.setCurrentLayerPosition(source.getCurrentLayerPosition());
        if (source.getLastActedBy() != null && !source.getLastActedBy().isBlank()) target.setLastActedBy(source.getLastActedBy());
        if (source.getLastActedRole() != null && !source.getLastActedRole().isBlank()) target.setLastActedRole(source.getLastActedRole());
        if (source.getLastActionRemarks() != null && !source.getLastActionRemarks().isBlank()) target.setLastActionRemarks(source.getLastActionRemarks());
        if (source.getLastActionType() != null) target.setLastActionType(source.getLastActionType());
        if (source.getReportingTo() != null) target.setReportingTo(source.getReportingTo());
       // if (source.getOverallProcessStatus() != null) target.setOverallProcessStatus(source.getOverallProcessStatus());
    }

    @Override
    public List<RecruitmentRequisitionMasterDto> getByCurrentStatus(List<String> statuses, String userId) {
        // 1. Convert String list to OverallProcessStatus Enum list with safe error handling
        List<OverallProcessStatus> overallStatuses = statuses.stream()
                .map(status -> {
                    try {
                        return OverallProcessStatus.valueOf(status.toUpperCase().trim());
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid Status provided: {}", status);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<RecruitmentRequisitionMaster> entities;

        // 2. Business Logic: If userId is provided, look into Group Action history (Join query)
        if (userId != null && !userId.trim().isEmpty()) {
            // This uses the custom JOIN query we discussed for the GroupAction table
            entities = masterRepo.findByOverallProcessStatusInAndActedUser(
                    overallStatuses, userId, RecordStatus.ACTIVE);
        } else {
            // Standard filter by status only
            entities = masterRepo.findByOverallProcessStatusInAndActedUser(
                    overallStatuses, userId,RecordStatus.ACTIVE);
        }

        // 3. Map entities to DTOs
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BaseResponse searchRequisitions(String fieldName, String searchValue) {
        BaseResponse response = new BaseResponse();

        // Specification তৈরি করা হচ্ছে
        Specification<RecruitmentRequisitionMaster> spec = (root, query, cb) -> {
            if (fieldName == null || searchValue == null || searchValue.trim().isEmpty()) {
                return cb.conjunction(); // কোনো ফিল্টার না থাকলে সব আসবে
            }

            // সার্চ প্যাটার্ন তৈরি (সব ছোট হাতের অক্ষরে সার্চ হবে যাতে Case-sensitive না হয়)
            String pattern = "%" + searchValue.toLowerCase() + "%";

            // Enhanced Switch Case
            return switch (fieldName) {
                case "requisitionCode" -> cb.like(cb.lower(root.get("requisitionCode")), pattern);
                case "userName" -> cb.like(cb.lower(root.get("userName")), pattern);
                case "positionName" -> cb.like(cb.lower(root.get("positionName")), pattern);
                case "companyName" -> cb.like(cb.lower(root.get("companyName")), pattern);
                case "personalAreaName" -> cb.like(cb.lower(root.get("personalAreaName")), pattern);

                // Enum ফিল্ডের জন্য (যেমন: Status)
                case "currentStatus" -> cb.like(cb.lower(root.get("currentStatus").as(String.class)), pattern);

                default -> cb.conjunction();
            };
        };

        // ডাটাবেস থেকে সার্চ রেজাল্ট নিয়ে আসা
        Long startTime = System.currentTimeMillis();
        List<RecruitmentRequisitionMaster> results = masterRepo.findAll(spec)
              //  .stream()
                .parallelStream()
                .filter(f-> f.getRecordStatus() != RecordStatus.DELETED).toList();

        Long endTime  = System.currentTimeMillis();

        System.out.println("Execution Time Of ==={}"+ (endTime-startTime));
        // DTO তে কনভার্ট করা (আপনার convertToDto মেথড থাকলে সেটি ব্যবহার করুন)
        List<RecruitmentRequisitionMasterDto> dtoList = results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        System.out.println("Search Api list Size====>{}"+dtoList.size());

        response.setRecruitmentRequisitionMasterDtos(dtoList);
        response.setMessage("Search results fetched successfully");
        return response;
    }

    private RecruitmentRequisitionMasterDto mapToDto(RecruitmentRequisitionMaster entity) {
        RecruitmentRequisitionMasterDto dto = new RecruitmentRequisitionMasterDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
