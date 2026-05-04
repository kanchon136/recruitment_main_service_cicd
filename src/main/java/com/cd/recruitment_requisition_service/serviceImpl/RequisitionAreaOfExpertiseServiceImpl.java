package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.dto.RequisitionAreaOfExpertiseDto;
import com.cd.recruitment_requisition_service.entity.*;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.ResponseEnum;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.ExpertiseEmployeeCategoryInfoParam;
import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseParam;
import com.cd.recruitment_requisition_service.param.RequisitionAreaOfExpertiseSkillParam;
import com.cd.recruitment_requisition_service.repository.ExpertiseEmployeeCategoryInfoRepository;
import com.cd.recruitment_requisition_service.repository.RecruitmentRequisitionMasterRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionAreaOfExpertiseRepository;
import com.cd.recruitment_requisition_service.repository.RequisitionAreaOfExpertiseSkillRepository;
import com.cd.recruitment_requisition_service.service.RequisitionAreaOfExpertiseService;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionAreaOfExpertiseServiceImpl implements RequisitionAreaOfExpertiseService {

    private final ExpertiseEmployeeCategoryInfoRepository categoryInfoRepository;
    private final RecruitmentRequisitionMasterRepository masterRepository;
    private final RequisitionAreaOfExpertiseRepository expertiseRepository;
    private final RequisitionAreaOfExpertiseSkillRepository skillRepository;

    public RequisitionAreaOfExpertiseServiceImpl(ExpertiseEmployeeCategoryInfoRepository categoryInfoRepository,
                                                 RecruitmentRequisitionMasterRepository masterRepository,
                                                 RequisitionAreaOfExpertiseRepository expertiseRepository,
                                                 RequisitionAreaOfExpertiseSkillRepository skillRepository) {
        this.categoryInfoRepository = categoryInfoRepository;
        this.masterRepository = masterRepository;
        this.expertiseRepository = expertiseRepository;
        this.skillRepository = skillRepository;
    }

    // --- SAVE ---
    @Override
    @Transactional
    public BaseResponse save(ExpertiseEmployeeCategoryInfoParam param) {
        BaseResponse response = new BaseResponse();

        RecruitmentRequisitionMaster parentMaster = masterRepository.findById(param.getRecruitmentRequisitionMasterId())
                .orElseThrow(() -> new CustomException("Master Requisition not found for ID: " + param.getRecruitmentRequisitionMasterId()));

        ExpertiseEmployeeCategoryInfo categoryInfo = new ExpertiseEmployeeCategoryInfo();
        BeanUtils.copyProperties(param, categoryInfo, "requisitionAreaOfExpertiseParams");
        categoryInfo.setRecruitmentRequisitionMaster(parentMaster);
        setBaseAuditFields(categoryInfo, param.getActedUserCode(), param.getActedUserName());

        if (param.getRequisitionAreaOfExpertiseParams() != null) {
            for (RequisitionAreaOfExpertiseParam expParam : param.getRequisitionAreaOfExpertiseParams()) {
                RequisitionAreaOfExpertise expertise = new RequisitionAreaOfExpertise();
                BeanUtils.copyProperties(expParam, expertise, "requisitionAreaOfExpertiseSkillParams");
                expertise.setEmployeeCategoryInfo(categoryInfo);
                setBaseAuditFields(expertise, param.getActedUserCode(), param.getActedUserName());

                if (expParam.getRequisitionAreaOfExpertiseSkillParams() != null) {
                    for (RequisitionAreaOfExpertiseSkillParam skillParam : expParam.getRequisitionAreaOfExpertiseSkillParams()) {
                        RequisitionAreaOfExpertiseSkill skill = new RequisitionAreaOfExpertiseSkill();
                        BeanUtils.copyProperties(skillParam, skill);
                        skill.setRequisitionAreaOfExpertise(expertise);
                        setBaseAuditFields(skill, param.getActedUserCode(), param.getActedUserName());
                        expertise.getSkills().add(skill);
                    }
                }
                categoryInfo.getAreaOfExpertises().add(expertise);
            }
        }

        ExpertiseEmployeeCategoryInfo saved = categoryInfoRepository.save(categoryInfo);
        response.setExpertiseEmployeeCategoryInfoDto(EntityAndDtoAllMapper.categoryInfoEntityToDto(saved));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    @Transactional
    public BaseResponse update(Long id, ExpertiseEmployeeCategoryInfoParam param) {
        BaseResponse response = new BaseResponse();

        // ১. মেইন ক্যাটাগরি (Master) খুঁজে বের করা
        ExpertiseEmployeeCategoryInfo existingCategory = categoryInfoRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Category Info record not found for ID: " + id));

        // ২. মাস্টার লেভেল আপডেট
        setIfNotNull(param.getEmployeeCategoryName(), existingCategory::setEmployeeCategoryName);
        setIfNotNull(param.getEmployeeCategoryCode(), existingCategory::setEmployeeCategoryCode);
        existingCategory.setUpdatedAt(LocalDateTime.now());
        existingCategory.setUpdatedBy(param.getActedUserCode());

        // ৩. চাইল্ড ডাটা হার্ড ডিলিট লজিক
        if (param.getRequisitionAreaOfExpertiseParams() != null) {

            // বিদ্যমান চাইল্ড এবং তাদের ডিপ চাইল্ড (Skills) ক্লিয়ার করা
            existingCategory.getAreaOfExpertises().forEach(e -> e.getSkills().clear());
            existingCategory.getAreaOfExpertises().clear();

            // ডাটাবেস থেকে পুরনো ডাটা পুরোপুরি মুছে ফেলা নিশ্চিত করা
            categoryInfoRepository.saveAndFlush(existingCategory);

            // ৪. নতুন ডাটা ইনসার্ট করা
            for (RequisitionAreaOfExpertiseParam expParam : param.getRequisitionAreaOfExpertiseParams()) {
                RequisitionAreaOfExpertise newExpertise = new RequisitionAreaOfExpertise();

            /* ভেরি ইম্পর্ট্যান্ট: "id" ইগনোর করা হয়েছে।
               যাতে প্যারামের পুরনো ID নতুন অবজেক্টে না আসে।
            */
                BeanUtils.copyProperties(expParam, newExpertise, "id", "requisitionAreaOfExpertiseSkillParams");
                newExpertise.setEmployeeCategoryInfo(existingCategory);

                setBaseAuditFields(newExpertise, param.getActedUserCode(), param.getActedUserName());
                newExpertise.setCreatedDateTime(LocalDateTime.now());
                newExpertise.setCreatedDate(LocalDate.now());
                newExpertise.setRecordStatus(RecordStatus.ACTIVE);

                // ৫. ডিপ লেভেল চাইল্ড (Skills) ইনসার্ট করা
                if (expParam.getRequisitionAreaOfExpertiseSkillParams() != null) {
                    List<RequisitionAreaOfExpertiseSkill> newSkills = expParam.getRequisitionAreaOfExpertiseSkillParams()
                            .stream()
                            .map(skillParam -> {
                                RequisitionAreaOfExpertiseSkill skill = new RequisitionAreaOfExpertiseSkill();
                                // এখানেও "id" ইগনোর করতে হবে
                                BeanUtils.copyProperties(skillParam, skill, "id");
                                skill.setRequisitionAreaOfExpertise(newExpertise);

                                setBaseAuditFields(skill, param.getActedUserCode(), param.getActedUserName());
                                skill.setRecordStatus(RecordStatus.ACTIVE);
                                skill.setCreatedDateTime(LocalDateTime.now());
                                return skill;
                            }).collect(Collectors.toList());

                    newExpertise.setSkills(newSkills);
                }

                existingCategory.getAreaOfExpertises().add(newExpertise);
            }
        }

        // ৬. ফাইনাল সেভ
        ExpertiseEmployeeCategoryInfo updated = categoryInfoRepository.save(existingCategory);

        response.setExpertiseEmployeeCategoryInfoDto(EntityAndDtoAllMapper.categoryInfoEntityToDto(updated));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    private void updateSkills(RequisitionAreaOfExpertise expertise, RequisitionAreaOfExpertiseParam expParam, ExpertiseEmployeeCategoryInfoParam mainParam) {
        if (expParam.getRequisitionAreaOfExpertiseSkillParams() != null) {
            Map<Long, RequisitionAreaOfExpertiseSkill> existingSkillMap = expertise.getSkills().stream()
                    .filter(s -> s.getId() != null)
                    .collect(Collectors.toMap(RequisitionAreaOfExpertiseSkill::getId, s -> s));

            List<RequisitionAreaOfExpertiseSkill> finalSkills = new ArrayList<>();

            for (RequisitionAreaOfExpertiseSkillParam sParam : expParam.getRequisitionAreaOfExpertiseSkillParams()) {
                RequisitionAreaOfExpertiseSkill skill;

                if (sParam.getId() != null && existingSkillMap.containsKey(sParam.getId())) {
                    skill = existingSkillMap.get(sParam.getId());

                    // 3. Skill Level Update (Exact Fields from your Param)
                    setIfNotNull(sParam.getSkillName(), skill::setSkillName);
                    setIfNotNull(sParam.getSkillCode(), skill::setSkillCode);
                } else {
                    skill = new RequisitionAreaOfExpertiseSkill();
                    BeanUtils.copyProperties(sParam, skill);
                    setBaseAuditFields(skill, mainParam.getActedUserCode(), mainParam.getActedUserName());
                }

                skill.setRequisitionAreaOfExpertise(expertise);
                skill.setUpdatedAt(LocalDateTime.now());
                skill.setUpdatedBy(mainParam.getUpdatedBy());
                finalSkills.add(skill);
            }
          //  expertise.getSkills().clear();
            expertise.getSkills().addAll(finalSkills);
        }
    }

    private <T> void setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Override
    public BaseResponse findAllByMasterId(Long masterId) {
        BaseResponse response = new BaseResponse();

        List<ExpertiseEmployeeCategoryInfo> categories = categoryInfoRepository
                .findAllByRecruitmentRequisitionMasterIdAndRecordStatus(masterId, RecordStatus.ACTIVE)
                .stream()
                .peek(category -> {
                    // ১. Expertise লিস্ট ফিল্টার করা (Nested Level 1)
                    if (category.getAreaOfExpertises() != null) {
                        List<RequisitionAreaOfExpertise> activeExpertises = category.getAreaOfExpertises().stream()
                                .filter(expertise -> expertise.getRecordStatus() == RecordStatus.ACTIVE)
                                .peek(expertise -> {
                                    // ২. Skill লিস্ট ফিল্টার করা (Nested Level 2)
                                    if (expertise.getSkills() != null) {
                                        expertise.setSkills(
                                                expertise.getSkills().stream()
                                                        .filter(skill -> skill.getRecordStatus() == RecordStatus.ACTIVE)
                                                        .collect(Collectors.toList())
                                        );
                                    }
                                })
                                .collect(Collectors.toList());

                        category.setAreaOfExpertises(activeExpertises);
                    }
                }).collect(Collectors.toList());

        // Entity থেকে DTO-তে কনভার্ট করা
        response.setExpertiseEmployeeCategoryInfoDtos(categories.stream()
                .map(EntityAndDtoAllMapper::categoryInfoEntityToDto)
                .collect(Collectors.toList()));

        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
    @Override
    public BaseResponse findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecordStatus(Long parentId) {
        return findAllByMasterId(parentId);
    }

    @Override
    public BaseResponse findById(Long id) {
        BaseResponse response = new BaseResponse();
        ExpertiseEmployeeCategoryInfo entity = categoryInfoRepository.findByIdAndRecordStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Record not found."));
        response.setExpertiseEmployeeCategoryInfoDto(EntityAndDtoAllMapper.categoryInfoEntityToDto(entity));
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    @Override
    public BaseResponse findAll() {
        BaseResponse response = new BaseResponse();
        List<ExpertiseEmployeeCategoryInfo> list = categoryInfoRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
        response.setExpertiseEmployeeCategoryInfoDtos(list.stream()
                .map(EntityAndDtoAllMapper::categoryInfoEntityToDto)
                .toList());
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }
    // --- DELETE ---
    @Override
    @Transactional
    public BaseResponse deleteById(Long categoryId, Long expertiseId, Long skillId) {
        BaseResponse response = new BaseResponse();
        LocalDateTime now = LocalDateTime.now();

        // 1. Delete specific Skill if skillId is provided
        if (skillId != null) {
            RequisitionAreaOfExpertiseSkill skill = skillRepository.findByIdAndRecordStatus(skillId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Skill not found for ID: " + skillId));

            skill.setRecordStatus(RecordStatus.DELETED);
            skill.setUpdatedAt(now);
            skillRepository.save(skill);
            response.setMessage("Skill deleted successfully.");
        }
        // 2. Delete specific Expertise and its nested Skills if expertiseId is provided
        else if (expertiseId != null) {
            RequisitionAreaOfExpertise expertise = expertiseRepository.findByIdAndRecordStatus(expertiseId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Expertise record not found for ID: " + expertiseId));

            expertise.setRecordStatus(RecordStatus.DELETED);
            expertise.setUpdatedAt(now);

            // Soft delete all nested skills
            expertise.getSkills().forEach(s -> {
                s.setRecordStatus(RecordStatus.DELETED);
                s.setUpdatedAt(now);
            });

            expertiseRepository.save(expertise);
            response.setMessage("Expertise and its associated skills deleted successfully.");
        }
        // 3. Delete Category and everything under it if only categoryId is provided
        else if (categoryId != null) {
            ExpertiseEmployeeCategoryInfo category = categoryInfoRepository.findByIdAndRecordStatus(categoryId, RecordStatus.ACTIVE)
                    .orElseThrow(() -> new CustomException("Category Info not found for ID: " + categoryId));

            category.setRecordStatus(RecordStatus.DELETED);
            category.setUpdatedAt(now);

            // Recursive soft delete: Category -> Expertise -> Skills
            category.getAreaOfExpertises().forEach(exp -> {
                exp.setRecordStatus(RecordStatus.DELETED);
                exp.setUpdatedAt(now);
                exp.getSkills().forEach(skill -> {
                    skill.setRecordStatus(RecordStatus.DELETED);
                    skill.setUpdatedAt(now);
                });
            });

            categoryInfoRepository.save(category);
            response.setMessage("Category and all associated data deleted successfully.");
        }
        else {
            throw new CustomException("No valid ID provided for deletion.");
        }

        return response;
    }
    // --- MISC / UNUSED FROM INTERFACE ---
    @Override
    public BaseResponse saveAll(List<RequisitionAreaOfExpertiseParam> requisitionAreaOfExpertiseParamList) {
        // Implement only if separate expertise list saving is required
        return new BaseResponse();
    }

    @Override
    public BaseResponse deleteById(Long id) {
        return deleteById(id, null, null);
    }

    @Override
    public BaseResponse findAllWithPagination(int pageNo, int pageSize) {
        BaseResponse response = new BaseResponse();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("createdDateTime").descending());
        Page<ExpertiseEmployeeCategoryInfo> page = categoryInfoRepository.findAllByRecordStatus(RecordStatus.ACTIVE, pageRequest);
        // Map to PaginatedResponse...
        response.setMessage(ResponseEnum.SUCCESS.getStatus());
        return response;
    }

    // --- HELPERS ---
    private void setBaseAuditFields(BaseEntity entity, String userCode, String userName) {
        entity.setRecordStatus(RecordStatus.ACTIVE);
        entity.setCreatedDate(LocalDate.now());
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setCreatedBy(userCode);
        entity.setActedUserName(userName);
    }
}
