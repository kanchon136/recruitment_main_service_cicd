package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionPendingAction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequisitionPendingActionRepository extends JpaRepository<RequisitionPendingAction, Long> {

    //  My Desk Logic: Find a specific user's pending action
    Optional<RequisitionPendingAction> findByRecruitmentRequisitionMasterIdAndPendingUserIdAndHasActedFalse(Long masterId, String userId);

    //  Mandatory Check: Find all mandatory pending actions for a layer
    List<RequisitionPendingAction> findAllByRecruitmentRequisitionMasterIdAndLayerPositionAndIsMandatoryTrue(Long masterId, Integer layerPosition);

    //  List View DTO: Check existence for 'Action Required By Me' flag
    boolean existsByRecruitmentRequisitionMasterIdAndPendingUserIdAndHasActedFalse(Long masterId, String userId);

    List<RequisitionPendingAction> findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(Long mId);

    boolean existsByRecruitmentRequisitionMasterIdAndLayerPositionAndIsMandatoryTrueAndHasActedFalse(Long mId, Integer layer);

    // Optional<RequisitionPendingAction> findAllByRecruitmentRequisitionMasterIdAndLayerPosition(Long masterId, Integer layer);
    List<RequisitionPendingAction> findAllByRecruitmentRequisitionMasterIdAndLayerPosition(Long masterId, Integer layer);

    boolean existsByRecruitmentRequisitionMasterIdAndLayerPositionAndHasActedFalse(Long mId, Integer layer);

    boolean existsByRecruitmentRequisitionMasterIdAndLayerPositionAndHasActedTrue(Long mId, Integer layer);

    boolean existsByRecruitmentRequisitionMasterIdAndLayerPosition(Long mId, Integer layer);

    @Modifying
    @Query("DELETE FROM RequisitionPendingAction pa WHERE pa.recruitmentRequisitionMaster.id = :mId AND pa.layerPosition >= :targetLayer")
    @Transactional
    void deleteByMasterIdAndLayerGreaterOrEqualTo(@Param("mId") Long mId, @Param("targetLayer") Integer targetLayer);

    //  Master ID onujayi shob pending action clean korar method
    @Modifying
    @Transactional
    @Query("DELETE FROM RequisitionPendingAction pa WHERE pa.recruitmentRequisitionMaster.id = :masterId")
    void deleteByRecruitmentRequisitionMasterId(@Param("masterId") Long masterId);

    Optional<RequisitionPendingAction> findByRecruitmentRequisitionMasterIdAndLayerPositionAndPendingUserId(Long id, Integer nextLayer, String nextRole);

    void deleteByRecruitmentRequisitionMasterIdAndLayerPositionAndPendingUserId(Long id, Integer currentLayer, String actedBy);

    void deleteByRecruitmentRequisitionMasterIdAndPendingUserId(Long id, String actedBy);

    List<RequisitionPendingAction> findAllByRecruitmentRequisitionMasterId(Long masterId);

    void deleteAllByRecruitmentRequisitionMasterId(Long id);
}
