package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionAgeAndNationality;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionAgeAndNationalityRepository extends JpaRepository<RequisitionAgeAndNationality, Long> {
    Optional<RequisitionAgeAndNationality> findByIdAndRecordStatus(Long id , RecordStatus recordStatus);
    List<RequisitionAgeAndNationality> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionAgeAndNationality> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionAgeAndNationality> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus,Long parentId);
    List<RequisitionAgeAndNationality> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus,Long parentId, RecordStatus parentStatus);
}
