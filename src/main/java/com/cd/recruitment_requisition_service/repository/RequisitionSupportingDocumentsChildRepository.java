package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionSupportingDocumentsChild;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionSupportingDocumentsChildRepository extends JpaRepository<RequisitionSupportingDocumentsChild,Long> {

    Optional<RequisitionSupportingDocumentsChild> findByIdAndRecordStatus(Long childId, RecordStatus recordStatus);

    List<RequisitionSupportingDocumentsChild> findAllByIdInAndRecordStatus(List<Long> childIds, RecordStatus recordStatus);
}
