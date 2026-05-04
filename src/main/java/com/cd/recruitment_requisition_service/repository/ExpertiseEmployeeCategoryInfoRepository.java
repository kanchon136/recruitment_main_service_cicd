package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.ExpertiseEmployeeCategoryInfo;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpertiseEmployeeCategoryInfoRepository extends JpaRepository<ExpertiseEmployeeCategoryInfo,Long> {
    Optional<ExpertiseEmployeeCategoryInfo> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);

    List<ExpertiseEmployeeCategoryInfo> findAllByRecruitmentRequisitionMasterIdAndRecordStatus(Long masterId, RecordStatus recordStatus);

    Page<ExpertiseEmployeeCategoryInfo> findAllByRecordStatus(RecordStatus recordStatus, PageRequest pageRequest);
    List<ExpertiseEmployeeCategoryInfo> findAllByRecordStatus(RecordStatus recordStatus);
     List<ExpertiseEmployeeCategoryInfo> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long masterId);

}
