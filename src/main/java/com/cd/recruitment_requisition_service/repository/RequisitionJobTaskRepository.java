package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionJobDescription;
import com.cd.recruitment_requisition_service.entity.RequisitionJobTask;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionJobTaskRepository extends JpaRepository<RequisitionJobTask,Long> {
    Optional<RequisitionJobTask> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionJobTask> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionJobTask> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

    // রিলেশন পাথ: RequisitionJobTask -> requisitionJobList -> requisitionJobDescription
    List<RequisitionJobTask> findAllByRecordStatusAndRequisitionJobList_RequisitionJobDescription_IdAndRecordStatus(
            RecordStatus taskStatus,
            Long descriptionId,
            RecordStatus descriptionStatus
    );
  //  List<RequisitionJobTask> findAllByRecordStatusAndRequisitionJobDescription_idAndRecordStatus(RecordStatus recordStatus, Long jobDescriptionId, RecordStatus parentStatus);
}
