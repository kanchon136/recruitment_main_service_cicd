package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertiseSkill;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionAreaOfExpertiseSkillRepository extends JpaRepository<RequisitionAreaOfExpertiseSkill,Long> {
    Optional<RequisitionAreaOfExpertiseSkill> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionAreaOfExpertiseSkill> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionAreaOfExpertiseSkill> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionAreaOfExpertiseSkill> findAllByRecordStatusAndRequisitionAreaOfExpertise_idAndRecordStatus(RecordStatus recordStatus, Long expertiseId, RecordStatus parentStatus);

    Optional<RequisitionAreaOfExpertiseSkill> findByIdAndRequisitionAreaOfExpertiseIdAndRecordStatus(Long childId, Long masterId, RecordStatus recordStatus);
}
