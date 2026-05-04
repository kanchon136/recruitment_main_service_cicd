package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionSupportingDocumentsMaster;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionSupportingDocumentsMasterRepository extends JpaRepository<RequisitionSupportingDocumentsMaster,Long> {
    Optional<RequisitionSupportingDocumentsMaster> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionSupportingDocumentsMaster> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionSupportingDocumentsMaster> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionSupportingDocumentsMaster> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);
    List<RequisitionSupportingDocumentsMaster> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentId, RecordStatus parentStatus);

    List<RequisitionSupportingDocumentsMaster> findAllByRecordStatusAndCreatedByAndRecruitmentRequisitionMaster_idAndRecordStatusAndCreatedBy(RecordStatus masterStatus, String actedUserId,Long masterId, RecordStatus documentRecordStatus, String documentUserId);
   List<RequisitionSupportingDocumentsMaster> findALlByRecordStatusAndCreatedBy(RecordStatus recordStatus , String logInUserId);
}
