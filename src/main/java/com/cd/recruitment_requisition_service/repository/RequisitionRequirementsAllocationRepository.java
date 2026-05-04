package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionRequirementsAllocation;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionRequirementsAllocationRepository extends JpaRepository<RequisitionRequirementsAllocation,Long> {
    Optional<RequisitionRequirementsAllocation> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);
    List<RequisitionRequirementsAllocation> findAllByRecordStatus(RecordStatus recordStatus);
    Page<RequisitionRequirementsAllocation> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);
    List<RequisitionRequirementsAllocation> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);
    List<RequisitionRequirementsAllocation> findAllByRecordStatusAndRecruitmentRequisitionMaster_idAndRecruitmentRequisitionMaster_recordStatus(RecordStatus recordStatus, Long parentId, RecordStatus parentStatus);

    boolean existsByRecruitmentRequisitionMasterIdAndShiftAllocationCodeAndPersonalSubAreaCode(
            Long masterId,
            String shiftCode,
            String subAreaCode
    );

    void deleteAllByRecruitmentRequisitionMasterId(Long masterId);

    boolean existsByRecruitmentRequisitionMasterId(Long masterId);
}
