package com.cd.recruitment_requisition_service.serviceImpl;

 import com.cd.recruitment_requisition_service.cumtomException.CustomException;
 import com.cd.recruitment_requisition_service.dto.RequisitionReplacementPersonDto;
 import com.cd.recruitment_requisition_service.entity.*;
 import com.cd.recruitment_requisition_service.enums.RecordStatus;
 import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
 import com.cd.recruitment_requisition_service.enums.RequisitionType;
 import com.cd.recruitment_requisition_service.enums.ResponseEnum;
 import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
 import com.cd.recruitment_requisition_service.param.RequisitionReplacementPersonParam;
 import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
 import com.cd.recruitment_requisition_service.repository.RequisitionGroupActionRepository;
 import com.cd.recruitment_requisition_service.repository.RequisitionReplacementPersonRepository;
 import com.cd.recruitment_requisition_service.service.RequisitionReplacementPersonService;
 import com.cd.recruitment_requisition_service.utils.ActionFormatterHelper;
 import com.cd.recruitment_requisition_service.utils.BaseResponse;
 import com.cd.recruitment_requisition_service.utils.PaginatedResponse;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.beans.BeanUtils;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Sort;
 import org.springframework.stereotype.Service;

 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.util.Comparator;
 import java.util.List;
 import java.util.Optional;
 import java.util.stream.Collectors;

@Service
 @Slf4j
public class RequisitionReplacementPersonServiceImpl implements RequisitionReplacementPersonService {


    private final RequisitionReplacementPersonRepository replacementPersonRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;
    private final RequisitionGroupActionRepository groupActionRepository;
    public RequisitionReplacementPersonServiceImpl(RequisitionReplacementPersonRepository replacementPersonRepository,
                                                   RecruitmentRequisitionMasterRepository masterRepository,
                                                   RequisitionGroupActionRepository groupActionRepository) {
        this.replacementPersonRepository = replacementPersonRepository;
        this.masterRepository = masterRepository;
        this.groupActionRepository = groupActionRepository;
    }


    // আপনার নতুন Param অনুযায়ী নন-নাল প্রপার্টি কপি লজিক
    private void copyNonNullProperties(RequisitionReplacementPersonParam source, RequisitionReplacementPerson target) {
        if (source.getEmployeeId() != null) target.setEmployeeId(source.getEmployeeId());
        if (source.getEmployeeName() != null) target.setEmployeeName(source.getEmployeeName());
        if (source.getDesignation() != null) target.setDesignation(source.getDesignation());
        if (source.getDepartmentName() != null) target.setDepartmentName(source.getDepartmentName());
        if (source.getDepartmentCode() != null) target.setDepartmentCode(source.getDepartmentCode());
    }

@Override // not used
public BaseResponse save(RequisitionReplacementPersonParam param) {
    BaseResponse response = new BaseResponse();

    // বর্তমান মাস্টার খুঁজে বের করা
    RecruitmentRequisitionMaster currentMaster = masterRepository
            .findById(param.getRecruitmentRequisitionMasterId())
            .orElseThrow(() -> new CustomException("Master not found"));

    // ১. ভ্যালিডেশন: টাইপ যদি REPLACE না হয়, তবে ডাটা সেভ হবে না
    if (currentMaster.getRequisitionType() != RequisitionType.REPLACE) {
        response.setMessage("Replacement persons can only be added to REPLACE type requisitions.");
        // আপনি চাইলে এখানে CustomException ও থ্রো করতে পারেন
        return response;
    }

    // ২. শুধুমাত্র REPLACE টাইপ হলেই নিচে আসবে
    RequisitionReplacementPerson entity = new RequisitionReplacementPerson();
    BeanUtils.copyProperties(param, entity, "id");

    /* লজিক: এই এমপ্লয়ি আইডি দিয়ে রিপ্লেসমেন্ট টেবিলে আগে কোনো রেকর্ড আছে কি না (বর্তমান মাস্টার সহ)
       যাতে আগের রিকুইজিশন আইডি এবং স্ট্যাটাস পাওয়া যায়।
    */
    Optional<RequisitionReplacementPerson> lastReplacementRecord = replacementPersonRepository
            .findFirstByEmployeeIdOrderByCreatedDateTimeDesc(param.getEmployeeId());

    if (lastReplacementRecord.isPresent()) {
        RecruitmentRequisitionMaster lastMaster = lastReplacementRecord.get().getRecruitmentRequisitionMaster();

        // আগের রিকুইজিশন আইডি সেট করা
        entity.setLastRequisitionId(lastMaster.getRequisitionCode());

        // সবশেষ অ্যাকশন থেকে ওপিনিয়ন ও স্ট্যাটাস বের করা
        Optional<RequisitionGroupAction> lastActionOpt = groupActionRepository
                .findFirstByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDescIdDesc(lastMaster.getId());

        if (lastActionOpt.isPresent()) {
            RequisitionGroupAction lastAction = lastActionOpt.get();
            entity.setOpinion(ActionFormatterHelper.getReportActionName(lastAction.getActionType().name()));
            entity.setCurrentProposedStatus(lastAction.getActedUserName() + " (" + lastAction.getCurrentRole() + ")");
        } else {
            entity.setOpinion("Draft / In Progress");
            entity.setCurrentProposedStatus(lastMaster.getCurrentStatus() != null ? lastMaster.getCurrentStatus().name() : "PENDING");
        }
    } else {
        // যদি একদমই নতুন হয় (আগে কখনোই এন্ট্রি হয়নি)
        entity.setOpinion("No previous history");
        entity.setCurrentProposedStatus("NEW_REPLACEMENT");
        entity.setLastRequisitionId("N/A");
    }

    // ৩. রিলেশন ও অডিট ডাটা সেট করে সেভ করা
    entity.setRecruitmentRequisitionMaster(currentMaster);
    entity.setCreatedDate(LocalDate.now());
    entity.setCreatedDateTime(LocalDateTime.now());
    entity.setCreatedBy(param.getActedUserCode());

    RequisitionReplacementPerson savedEntity = replacementPersonRepository.save(entity);

    response.setRequisitionReplacementPersonDto(convertToDto(savedEntity));
    response.setMessage(ResponseEnum.SUCCESS.getStatus());
    return response;
}
    @Override // it is used by frontend
    public BaseResponse saveAll(List<RequisitionReplacementPersonParam> params) {
        BaseResponse response = new BaseResponse();

        if (params == null || params.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        List<RequisitionReplacementPerson> entitiesToSave = params.stream()
                .map(param -> {
                    RecruitmentRequisitionMaster currentMaster = masterRepository
                            .findById(param.getRecruitmentRequisitionMasterId())
                            .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

                    if (currentMaster.getRequisitionType() != RequisitionType.REPLACE) {
                        throw new CustomException("Replacement persons can only be added to REPLACE type requisitions. Master Code: " + currentMaster.getRequisitionCode());
                    }

                    RequisitionReplacementPerson entity = new RequisitionReplacementPerson();
                    BeanUtils.copyProperties(param, entity, "id", "recruitmentRequisitionMasterId");

                    // ১. আগের রিপ্লেসমেন্ট হিস্ট্রি চেক (সবচেয়ে লেটেস্ট রেকর্ড)
                    Optional<RequisitionReplacementPerson> lastReplacementRecord = replacementPersonRepository
                            .findFirstByEmployeeIdOrderByCreatedDateTimeDesc(param.getEmployeeId());

                    if (lastReplacementRecord.isPresent()) {
                        RecruitmentRequisitionMaster lastMaster = lastReplacementRecord.get().getRecruitmentRequisitionMaster();
                        entity.setLastRequisitionId(lastMaster.getRequisitionCode());

                        // ২. সবশেষ গ্রুপ অ্যাকশন চেক
                        Optional<RequisitionGroupAction> lastActionOpt = groupActionRepository
                                .findFirstByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDescIdDesc(lastMaster.getId());

                        if (lastActionOpt.isPresent()) {
                            RequisitionGroupAction lastAction = lastActionOpt.get();
                            entity.setOpinion(ActionFormatterHelper.getReportActionName(lastAction.getActionType().name()));

                            // ৩. কন্ডিশন: অ্যাকশন হয়ে থাকলে RecommendedAllocations থেকে লেটেস্ট ভ্যালু নিবে
                            if (lastAction.getRecommendedAllocations() != null && !lastAction.getRecommendedAllocations().isEmpty()) {
                                lastAction.getRecommendedAllocations().stream()
                                        .max(Comparator.comparing(RecommendedAllocations::getCreatedDateTime))
                                        .ifPresentOrElse(
                                                recAlloc -> entity.setCurrentProposedStatus(recAlloc.getRecomendedHeadCount() != null ? String.valueOf(recAlloc.getRecomendedHeadCount()) : null),
                                                () -> entity.setCurrentProposedStatus(null)
                                        );
                            } else {
                                // যদি কালেকশনে ডাটা না থাকে তবে মাস্টার লেভেলের রিকমেন্ডেড হেডকাউন্ট (যদি থাকে)
                                entity.setCurrentProposedStatus(lastAction.getRecommendedHeadcount() != null ? String.valueOf(lastAction.getRecommendedHeadcount()) : null);
                            }
                        } else {
                            // ৪. কন্ডিশন: অ্যাকশন না হয়ে থাকলে মূল Allocations থেকে লেটেস্ট ভ্যালু নিবে
                            if (lastMaster.getAllocations() != null && !lastMaster.getAllocations().isEmpty()) {
                                lastMaster.getAllocations().stream()
                                        .max(Comparator.comparing(RequisitionRequirementsAllocation::getCreatedDateTime))
                                        .ifPresentOrElse(
                                                lastAlloc -> entity.setCurrentProposedStatus(String.valueOf(lastAlloc.getNoOfRequirements())),
                                                () -> entity.setCurrentProposedStatus(null)
                                        );
                            } else {
                                entity.setCurrentProposedStatus(null);
                            }
                        }
                    } else {
                        // একদম নতুন হলে সব null
                        entity.setLastRequisitionId(null);
                        entity.setOpinion(null);
                        entity.setCurrentProposedStatus(null);
                    }

                    entity.setRecruitmentRequisitionMaster(currentMaster);
                    entity.setRecordStatus(RecordStatus.ACTIVE);
                    entity.setCreatedDate(LocalDate.now());
                    entity.setCreatedDateTime(LocalDateTime.now());
                    entity.setCreatedBy(param.getActedUserCode());

                    return entity;
                })
                .collect(Collectors.toList());

        List<RequisitionReplacementPerson> savedEntities = replacementPersonRepository.saveAll(entitiesToSave);
        List<RequisitionReplacementPersonDto> dtoList = savedEntities.stream().map(this::convertToDto).collect(Collectors.toList());

        response.setRequisitionReplacementPersonDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
    @Override
    public BaseResponse update(Long id, RequisitionReplacementPersonParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionReplacementPerson existing = replacementPersonRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Replacement Person not found for ID: " + id));

        copyNonNullProperties(param, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionReplacementPerson updatedEntity = replacementPersonRepository.save(existing);
        response.setRequisitionReplacementPersonDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionReplacementPerson existing = replacementPersonRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Replacement Person not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        existing.setUpdatedAt(LocalDateTime.now());
        replacementPersonRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionReplacementPerson entity = replacementPersonRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Replacement Person not found for ID: " + id));

        response.setRequisitionReplacementPersonDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllByRequisitionMasterId(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionReplacementPerson> entityList = replacementPersonRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, masterId);

        List<RequisitionReplacementPersonDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionReplacementPersonDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionReplacementPerson> entityList = replacementPersonRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionReplacementPersonDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionReplacementPersonDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionReplacementPerson> entityPage = replacementPersonRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);

        // PaginatedResponse ম্যাপ করার জন্য entityPage.map ব্যবহার করা হয়েছে
        PaginatedResponse<RequisitionReplacementPersonDto> paginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(this::convertToDto));

        response.setRequisitionReplacementPersonDtoPaginatedResponse(paginatedResponse);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    private RequisitionReplacementPersonDto convertToDto(RequisitionReplacementPerson entity) {

        RequisitionReplacementPersonDto dto = EntityAndDtoAllMapper.
                requisitionReplacementPersonEntityToDto(entity);

        dto.setApprovalFrequency((int) 0);

        return dto;
    }
}
