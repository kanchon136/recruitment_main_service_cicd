package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionAdditionalRequirements;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionAdditionalRequirementsRepository extends JpaRepository<RequisitionAdditionalRequirements, Long> {
    Optional<RequisitionAdditionalRequirements> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionAdditionalRequirements> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionAdditionalRequirements> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);
    List<RequisitionAdditionalRequirements> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);
    List<RequisitionAdditionalRequirements> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentId, RecordStatus parentStatus);
}
