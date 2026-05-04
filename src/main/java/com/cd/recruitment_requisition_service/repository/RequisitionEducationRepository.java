package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionEducation;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionEducationRepository extends JpaRepository<RequisitionEducation,Long> {
    Optional<RequisitionEducation> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionEducation> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionEducation> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionEducation> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus,Long parentId);
    List<RequisitionEducation> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus,Long parentId,RecordStatus parentStatus);
}
