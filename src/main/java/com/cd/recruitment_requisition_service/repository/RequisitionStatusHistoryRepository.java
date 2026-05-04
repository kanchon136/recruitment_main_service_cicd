package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequisitionStatusHistoryRepository extends JpaRepository<RequisitionStatusHistory, Long> {

    List<RequisitionStatusHistory> findByRecruitmentRequisitionMasterIdOrderByActionTimeAsc(Long masterRequisitionId);

    List<RequisitionStatusHistory> findAllByRecruitmentRequisitionMasterIdOrderByActionTimeDesc(Long masterId);
}
