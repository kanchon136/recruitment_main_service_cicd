package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionAreaOfExpertise;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionAreaOfExpertiseRepository extends JpaRepository<RequisitionAreaOfExpertise,Long> {

    Optional<RequisitionAreaOfExpertise> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionAreaOfExpertise> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionAreaOfExpertise> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);
   // List<RequisitionAreaOfExpertise> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long masterId);
   // List<RequisitionAreaOfExpertise> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long masterId,RecordStatus parentStatus);
}
