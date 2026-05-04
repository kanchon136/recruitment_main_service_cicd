package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionJustification;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionJustificationRepository extends JpaRepository<RequisitionJustification, Long> {

    Optional<RequisitionJustification> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionJustification> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionJustification> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable );
    List<RequisitionJustification> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);
    List<RequisitionJustification> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentId, RecordStatus parentStatus);




}