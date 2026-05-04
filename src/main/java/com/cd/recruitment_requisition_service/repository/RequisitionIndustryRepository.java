package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionIndustry;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionIndustryRepository extends JpaRepository<RequisitionIndustry, Long> {

    Optional<RequisitionIndustry> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionIndustry> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionIndustry> findAllByRecordStatus(RecordStatus recordStatus , Pageable pageable);
    List<RequisitionIndustry> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);
    List<RequisitionIndustry> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentId, RecordStatus parentStatus);


}
