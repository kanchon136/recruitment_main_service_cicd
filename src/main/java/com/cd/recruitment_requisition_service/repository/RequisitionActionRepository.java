package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionAction;
import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequisitionActionRepository extends JpaRepository<RequisitionAction, Long> {

    List<RequisitionAction> findAllByRecruitmentRequisitionMaster_id(Long masterRequisitionId);

    Optional<RequisitionAction> findTopByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDesc(Long mId);

    List<RequisitionAction> findAllByRecruitmentRequisitionMasterId(Long masterId);

    Optional<RequisitionAction> findTopByRecruitmentRequisitionMasterIdAndToLayerPositionAndToStageAndActionTypeInOrderByIdDesc(
            Long masterId,
            Integer toLayerPosition,
            String toStage, // এখানে 'toStage' হলো বর্তমান ইউজারের আইডি (actedBy)
            List<RequisitionActionType> actionTypes
    );

    @Query("SELECT ra FROM RequisitionAction ra WHERE ra.recruitmentRequisitionMaster.id = :masterId ORDER BY ra.createdDateTime DESC")
    List<RequisitionAction> findAllByMasterIdOrderByCreatedDateTimeDesc(@Param("masterId") Long masterId);

}
