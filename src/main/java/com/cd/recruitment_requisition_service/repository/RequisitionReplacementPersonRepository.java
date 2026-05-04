package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionReplacementPerson;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequisitionReplacementPersonRepository extends JpaRepository<RequisitionReplacementPerson,Long> {

     Optional<RequisitionReplacementPerson> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);

     List<RequisitionReplacementPerson> findAllByRecordStatus(RecordStatus recordStatus);

     Page<RequisitionReplacementPerson> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

     List<RequisitionReplacementPerson> findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus recordStatus, Long parentId);

    // long countByEmployeeIdAndRecordStatusAndRequisitionIdIsNotNull(String employeeId, RecordStatus recordStatus);

//     // মাস্টারের স্ট্যাটাস এবং রিপ্লেসমেন্ট পারসনের রিকুইজিশন আইডি--উভয়ই চেক করবে
//     long countByEmployeeIdAndRecordStatusAndRequisitionIdIsNotNullAndRecruitmentRequisitionMaster_CurrentStatus(
//             String employeeId,
//             RecordStatus recordStatus,
//             RequisitionStatus masterCurrentStatus
//     );


     Optional<RequisitionReplacementPerson> findFirstByEmployeeIdAndRecruitmentRequisitionMaster_IdNotOrderByCreatedDateTimeDesc(
             String employeeId, Long currentMasterId
     );

     Optional<RequisitionReplacementPerson> findFirstByEmployeeIdOrderByCreatedDateTimeDesc(String employeeId);

}
