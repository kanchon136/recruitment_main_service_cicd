package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RequisitionGroupAction;
import com.cd.recruitment_requisition_service.enums.RequisitionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequisitionGroupActionRepository extends JpaRepository<RequisitionGroupAction, Long> {
//    // Finds all individual actions taken within a specific layer for a master requisition
//    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannelId(
//            Long masterRequisitionId, Long requisitionApprovedChannelId);
//
//    // Finds all distinct user actions taken for a specific layer
//    @Query("SELECT DISTINCT rga.actedBy FROM RequisitionGroupAction rga " +
//            "WHERE rga.recruitmentRequisitionMaster.id = :masterId AND rga.requisitionApprovedChannel.id = :channelId")
//    List<String> findDistinctActedByUsers(Long masterId, Long channelId);
//
//    // Checks if a specific user has already acted on this layer/master combination
//    boolean existsByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannelIdAndActedBy(
//            Long masterId, Long channelId, String actedBy);
//
//    List<Long> findAllChannelIdsByMasterIdAndLayerPosition(Long masterId, Integer layerPosition);
//    List<String> findDistinctActedByUsersByChannelIds(Long masterId, List<Long> channelIds);
//
//    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannel_LayerPosition(
//            Long masterRequisitionId,
//            Integer layerPosition
//    );

    /**
     * Checks if a user has already acted on a specific RequisitionApprovedChannel instance.
     * Used to enforce the URS rule: one person acts once per assigned channel.
     */
//    boolean existsByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannelIdAndActedBy(
//            Long masterId, Long channelId, String actedBy);

    /**
     * Note: The previous simple derived method 'findAllByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannelId'
     * is redundant if we use the final method below for layer details, but you can keep it if needed for specific channel lookup.
     */

    /**
     *  CORRECTED: Retrieves the distinct list of 'actedBy' users who have taken action on ANY
     * of the channels within the list provided.
     * This is the SECOND step of the checkLayerCompletion() logic: "How many Unique users acted?"
     * Uses a HQL IN clause to check against the list of channel IDs returned by the other repository.
     */
//    @Query("SELECT DISTINCT rga.actedBy FROM RequisitionGroupAction rga " +
//            "WHERE rga.recruitmentRequisitionMaster.id = :masterId " +
//            "AND rga.requisitionApprovedChannel.id IN :channelIds")
//    List<String> findDistinctActedByUsersByChannelIds(
//            @Param("masterId") Long masterId,
//            @Param("channelIds") List<Long> channelIds);

    /**
     *  CORRECTED (For Show Layer Details): Retrieves all individual actions (Group Actions)
     * for a specific master requisition and a specific layer position.
     * This derived query works because it navigates the association: RequisitionGroupAction -> requisitionApprovedChannel -> layerPosition.
     */
//    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterIdAndRequisitionApprovedChannel_LayerPosition(
//            Long masterRequisitionId,
//            Integer layerPosition
//    );

    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterId(Long id);
    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeAsc(Long masterId);

    Optional<RequisitionGroupAction> findFirstByRecruitmentRequisitionMasterIdOrderByActionDateTimeDesc(Long masterId);


    Optional<RequisitionGroupAction> findFirstByRecruitmentRequisitionMasterIdAndActedUserCodeAndLayerPositionOrderByCreatedDateTimeDesc(Long masterId, String memberCode, Integer layerPosition);

    List<RequisitionGroupAction> findAllByRecruitmentRequisitionMasterIdAndLayerPosition(Long masterId, Integer layerPos);

    // Naming Query: রিপ্লেসমেন্ট পারসনের employeeId এবং মাস্টারের RequisitionType দিয়ে লেটেস্ট ১টি রেকর্ড আনা
    Optional<RequisitionGroupAction> findFirstByRecruitmentRequisitionMaster_ReplacementPersons_EmployeeIdAndRecruitmentRequisitionMaster_RequisitionTypeOrderByCreatedDateTimeDescIdDesc(
            String employeeId,
            RequisitionType requisitionType
    );


    Optional<RequisitionGroupAction> findFirstByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDescIdDesc(Long id);
}
