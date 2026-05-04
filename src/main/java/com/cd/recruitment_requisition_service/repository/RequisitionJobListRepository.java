package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionJobList;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequisitionJobListRepository extends JpaRepository<RequisitionJobList,Long> {
    Optional<RequisitionJobList> findByIdAndRecordStatus(Long id, RecordStatus status);

}
