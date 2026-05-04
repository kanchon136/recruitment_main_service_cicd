package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionJobDescription;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionJobDescriptionRepository extends JpaRepository<RequisitionJobDescription,Long> {
  Optional<RequisitionJobDescription> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
  List<RequisitionJobDescription> findAllByRecordStatus(RecordStatus recordStatus);
  Page<RequisitionJobDescription> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
  List<RequisitionJobDescription> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentMaterId);
  List<RequisitionJobDescription> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentMaterId,RecordStatus parentStatus);
}
