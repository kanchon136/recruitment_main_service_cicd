package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionApprovedChannelDto;
import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.entity.RequisitionApprovedChannel;
import com.cd.recruitment_requisition_service.enums.AuthorizationType;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.RequisitionApprovedChannelParam;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionApprovedChannelRepository;
import com.cd.recruitment_requisition_service.service.RequisitionApprovedChannelService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import com.cd.recruitment_requisition_service.utils.DynamicCodeGenerator;
import com.cd.recruitment_requisition_service.utils.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionApprovedChannelServiceImpl implements RequisitionApprovedChannelService {
    private final RequisitionApprovedChannelRepository channelRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;
    private final DynamicCodeGenerator dynamicCodeGenerator;

    public RequisitionApprovedChannelServiceImpl(RequisitionApprovedChannelRepository channelRepository,
                                                 RecruitmentRequisitionMasterRepository masterRepository,
                                                 DynamicCodeGenerator dynamicCodeGenerator) {
        this.channelRepository = channelRepository;
        this.masterRepository = masterRepository;
        this.dynamicCodeGenerator = dynamicCodeGenerator;
    }

    private RequisitionApprovedChannelDto convertToDto(RequisitionApprovedChannel entity) {
         return EntityAndDtoAllMapper.requisitionApprovedChannelEntityToDto(entity);
    }

//    @Override
//    public BaseResponse save(RequisitionApprovedChannelParam param) {
//        BaseResponse response = new BaseResponse();
//
//        RecruitmentRequisitionMaster parent = masterRepository
//                .findById(param.getRecruitmentRequisitionMasterId())
//                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));
//
//        RequisitionApprovedChannel channel = new RequisitionApprovedChannel();
//
//        BeanUtils.copyProperties(param, channel, "id", "recruitmentRequisitionMasterId");
//        channel.setMemberCode(dynamicCodeGenerator.generateCode("rq_apv_chanel_sec","CH","ddMMyyyy"));
//
//        channel.setRecruitmentRequisitionMaster(parent);
//
//        channel.setRecordStatus(RecordStatus.ACTIVE);
//         channel.setCreatedDate(LocalDate.now());
//        channel.setCreatedDateTime(LocalDateTime.now());
//        channel.setCreatedBy(param.getActedUserCode());
//        channel.setActedUserName(param.getActedUserName());
//
//        RequisitionApprovedChannel savedEntity = channelRepository.save(channel);
//
//        response.setRequisitionApprovedChannelDto(convertToDto(savedEntity));
//        response.setMessage(ResponseEnum.SUCCESS.getStatus());
//        return response;
//    }

    @Override
    public BaseResponse save(RequisitionApprovedChannelParam param) {
        BaseResponse response = new BaseResponse();

        // 1. Validation: INITIATOR must be at Layer Position 0
        if (AuthorizationType.INITIATOR.equals(param.getAuthorizationType())) {
            if (param.getLayerPosition() == null || param.getLayerPosition() != 0) {
                throw new CustomException("Invalid Parameter: An INITIATOR must always be assigned to Layer Position 0.");
            }
        }

        // 2. Validation: Other types cannot occupy Layer Position 0
        else if (param.getLayerPosition() != null && param.getLayerPosition() == 0) {
            throw new CustomException("Invalid Parameter: Only the INITIATOR can occupy Layer Position 0. Other roles must start from Layer 1 or higher.");
        }

        // Fetch Master Entity
        RecruitmentRequisitionMaster parent = masterRepository
                .findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Requisition Master not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        // 3. Duplicate Check: Ensure only one INITIATOR/Layer 0 exists for this Master ID
        if (AuthorizationType.INITIATOR.equals(param.getAuthorizationType())) {
            boolean isInitiatorExists = channelRepository.existsByRecruitmentRequisitionMasterIdAndAuthorizationType(
                    param.getRecruitmentRequisitionMasterId(), AuthorizationType.INITIATOR);

            if (isInitiatorExists) {
                throw new CustomException("Duplicate Record: An INITIATOR has already been defined for this requisition. You cannot add more than one.");
            }
        }

        // Map and Save
        RequisitionApprovedChannel channel = new RequisitionApprovedChannel();
        BeanUtils.copyProperties(param, channel, "id", "recruitmentRequisitionMasterId");

        channel.setMemberCode(dynamicCodeGenerator.generateCode("rq_apv_chanel_sec","CH","ddMMyyyy"));
        channel.setRecruitmentRequisitionMaster(parent);
        channel.setRecordStatus(RecordStatus.ACTIVE);
        channel.setCreatedDate(LocalDate.now());
        channel.setCreatedDateTime(LocalDateTime.now());
        channel.setCreatedBy(param.getActedUserCode());
        channel.setActedUserName(param.getActedUserName());

        RequisitionApprovedChannel savedEntity = channelRepository.save(channel);

        response.setRequisitionApprovedChannelDto(convertToDto(savedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }



    @Override
    @Transactional
    public BaseResponse saveAll(List<RequisitionApprovedChannelParam> params) {

        BaseResponse response = new BaseResponse();

        if (params == null || params.isEmpty()) {
            response.setMessage("Input list cannot be empty.");
            return response;
        }

        // --- Strict Validation for INITIATOR and Layer 0 ---

        // ১. চেক করা হচ্ছে লিস্টে কয়টি INITIATOR (Layer 0) আছে
        long initiatorCount = params.stream()
                .filter(p -> AuthorizationType.INITIATOR.equals(p.getAuthorizationType())
                        && p.getLayerPosition() != null
                        && p.getLayerPosition() == 0)
                .count();

        // ২. যদি একটির বেশি ইনিশিয়েটর থাকে
        if (initiatorCount > 1) {
            throw new CustomException("Validation Error: Multiple INITIATORS found. You can only have one INITIATOR at Layer Position 0.");
        }

        // ৩. যদি কোনো ইনিশিয়েটর না থাকে (এটি বাধ্যতামূলক হলে)
        if (initiatorCount == 0) {
            throw new CustomException("Validation Error: An INITIATOR with Layer Position 0 is mandatory for this process.");
        }

        // ৪. অন্য কোনো রোলে ভুল করে Layer 0 দেওয়া হয়েছে কি না তা চেক করা
        boolean invalidLayerZero = params.stream()
                .anyMatch(p -> !AuthorizationType.INITIATOR.equals(p.getAuthorizationType())
                        && p.getLayerPosition() != null
                        && p.getLayerPosition() == 0);

        if (invalidLayerZero) {
            throw new CustomException("Validation Error: Layer Position 0 is reserved strictly for the INITIATOR.");
        }

        // ৫. ইনিশিয়েটর টাইপ কিন্তু লেয়ার ০ না এমন ভুল চেক করা
        boolean initiatorWrongLayer = params.stream()
                .anyMatch(p -> AuthorizationType.INITIATOR.equals(p.getAuthorizationType())
                        && (p.getLayerPosition() == null || p.getLayerPosition() != 0));

        if (initiatorWrongLayer) {
            throw new CustomException("Validation Error: Role 'INITIATOR' must always be assigned to Layer Position 0.");
        }

         Set<Long> masterIds = params.stream()
                .map(RequisitionApprovedChannelParam::getRecruitmentRequisitionMasterId)
                .collect(Collectors.toSet());

         Map<Long, RecruitmentRequisitionMaster> masterCache = new HashMap<>();

        for (Long masterId : masterIds) {

            RecruitmentRequisitionMaster master = masterRepository.findById(masterId)
                    .orElseThrow(() -> new CustomException(
                            "Requisition Master not found for ID: " + masterId));

            masterCache.put(masterId, master);

             channelRepository.deleteAllByRecruitmentRequisitionMaster_Id(masterId);
        }

         List<RequisitionApprovedChannel> entitiesToSave = params.stream()
                .map(param -> {

                    RequisitionApprovedChannel channel = new RequisitionApprovedChannel();

                    BeanUtils.copyProperties(param, channel, "id", "recruitmentRequisitionMasterId");

                    channel.setRecruitmentRequisitionMaster(
                            masterCache.get(param.getRecruitmentRequisitionMasterId()));

                    channel.setRecordStatus(RecordStatus.ACTIVE);
                    channel.setCreatedDate(LocalDate.now());
                    channel.setCreatedDateTime(LocalDateTime.now());
                    channel.setCreatedBy(param.getActedUserCode());
                    channel.setActedUserName(param.getActedUserName());

                    return channel;
                })
                .collect(Collectors.toList());

        List<RequisitionApprovedChannel> savedEntities =
                channelRepository.saveAll(entitiesToSave);

        response.setRequisitionApprovedChannelDtos(
                savedEntities.stream().map(this::convertToDto).toList());

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse update(Long id, RequisitionApprovedChannelParam param) {
        BaseResponse response = new BaseResponse();

        RequisitionApprovedChannel existing = channelRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Approved Channel not found for ID: " + id));

        copyNonNullProperties(param, existing);

        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(param.getUpdatedBy());

        RequisitionApprovedChannel updatedEntity = channelRepository.save(existing);

        response.setRequisitionApprovedChannelDto(convertToDto(updatedEntity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse deleteById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionApprovedChannel existing = channelRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Approved Channel not found for ID: " + id));

        existing.setRecordStatus(RecordStatus.DELETED);
        // existing.setUpdatedBy(currentUserId);
        existing.setUpdatedAt(LocalDateTime.now());
        channelRepository.save(existing);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();

        RequisitionApprovedChannel entity = channelRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Approved Channel not found for ID: " + id));

        response.setRequisitionApprovedChannelDto(convertToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findByRequisitionMasterId(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<RequisitionApprovedChannel> entityList = channelRepository
                .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, masterId);

        List<RequisitionApprovedChannelDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionApprovedChannelDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }


    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<RequisitionApprovedChannel> entityList = channelRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        List<RequisitionApprovedChannelDto> dtoList = entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        response.setRequisitionApprovedChannelDtos(dtoList);
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<RequisitionApprovedChannel> entityPage = channelRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);
        PaginatedResponse<RequisitionApprovedChannelDto> requisitionApprovedChannelDtoPaginatedResponse =
                PaginatedResponse.fromPage(entityPage.map(EntityAndDtoAllMapper::requisitionApprovedChannelEntityToDto));
        response.setRequisitionApprovedChannelDtoPaginatedResponse(requisitionApprovedChannelDtoPaginatedResponse);

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void copyNonNullProperties(RequisitionApprovedChannelParam source, RequisitionApprovedChannel target) {
        if (source.getMemberCode() != null) target.setMemberCode(source.getMemberCode());
        if (source.getProcessTitle() != null) target.setProcessTitle(source.getProcessTitle());
        if (source.getLayerPosition() != null) target.setLayerPosition(source.getLayerPosition());
        if (source.getApprovedType() != null) target.setApprovedType(source.getApprovedType());
        if (source.getPanelMember() != null) target.setPanelMember(source.getPanelMember());
        if (source.getDesignation() != null) target.setDesignation(source.getDesignation());
    }
}
