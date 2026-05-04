package com.cd.recruitment_requisition_service.repository;

import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.enums.RecordStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruitmentRequisitionMasterRepository extends JpaRepository<RecruitmentRequisitionMaster, Long>, JpaSpecificationExecutor<RecruitmentRequisitionMaster> {
    Optional<RecruitmentRequisitionMaster> findByIdAndRecordStatus(Long id, RecordStatus recordStatus);

    List<RecruitmentRequisitionMaster> findAllByRecordStatus(RecordStatus recordStatus);

    Page<RecruitmentRequisitionMaster> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

    List<RecruitmentRequisitionMaster> findAllByRecordStatusAndCurrentStatusAndUserId(RecordStatus recordStatus, RequisitionStatus currentStatus, String userId); // it is for when create requisition then show won stage

    // 1. Scheduler Rule: 90 days Postpone Auto-Withdrawal

    /**
     * Finds requisitions that are currently POSTPONED and have not been updated since the cutoff date.
     * Assuming 'updatedAt' reflects the last status change/activity time.
     *
     * @param status     The target status (POSTPONED).
     * @param cutOffDate The date 90 days ago.
     * @return List of overdue requisitions.
     */
    List<RecruitmentRequisitionMaster> findAllByCurrentStatusAndUpdatedAtBefore(
            RequisitionStatus status,
            LocalDateTime cutOffDate);

//    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
//            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
//            "AND (" +
//            "   r.userId = :userId " +                                       // 1. User is the Creator (Data Entry Operator)
//            "   OR EXISTS (SELECT 1 FROM RequisitionApprovedChannel ac " +
//            "              WHERE ac.recruitmentRequisitionMaster = r " +
//            "              AND ac.memberCode = :userId " +
//            "              AND ac.layerPosition = 0) " +                     // 2. User is the designated RAISER (Layer 0)
//            "   OR EXISTS (SELECT 1 FROM RequisitionPendingAction pa " +
//            "              WHERE pa.recruitmentRequisitionMaster = r " +
//            "              AND pa.pendingUserId = :userId " +
//            "              AND pa.hasActed = false) " +                      // 3. Currently pending at user's desk
//            "   OR EXISTS (SELECT 1 FROM RequisitionGroupAction ga " +
//            "              WHERE ga.recruitmentRequisitionMaster = r " +
//            "              AND ga.actedUserCode = :userId)" +                // 4. User has previously taken action
//            ")")
//    List<RecruitmentRequisitionMaster> findTrackingRequisitions(@Param("userId") String userId);

    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
            "LEFT JOIN RequisitionGroupAction ga ON ga.recruitmentRequisitionMaster = r " +
            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
            "AND (" +
            "   r.userId = :userId " +
            "   OR EXISTS (SELECT 1 FROM RequisitionApprovedChannel ac " +
            "              WHERE ac.recruitmentRequisitionMaster = r " +
            "              AND ac.memberCode = :userId " +
            "              AND ac.layerPosition = 0) " +
            "   OR EXISTS (SELECT 1 FROM RequisitionPendingAction pa " +
            "              WHERE pa.recruitmentRequisitionMaster = r " +
            "              AND pa.pendingUserId = :userId " +
            "              AND pa.hasActed = false) " +
            "   OR EXISTS (SELECT 1 FROM RequisitionGroupAction ga2 " +
            "              WHERE ga2.recruitmentRequisitionMaster = r " +
            "              AND ga2.actedUserCode = :userId)" +
            ") " +
            "GROUP BY r " +
            "ORDER BY MAX(ga.createdDateTime) DESC NULLS LAST, r.createdDateTime DESC")
    List<RecruitmentRequisitionMaster> findTrackingRequisitions(@Param("userId") String userId);


//    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
//            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
//            "AND r.overallProcessStatus IN :statusList " +  // নতুন স্ট্যাটাস ফিল্টার
//            "AND (" +
//            "   r.userId = :userId " +
//            "   OR EXISTS (SELECT 1 FROM RequisitionApprovedChannel ac " +
//            "              WHERE ac.recruitmentRequisitionMaster = r " +
//            "              AND ac.memberCode = :userId " +
//            "              AND ac.layerPosition = 0) " +
//            "   OR EXISTS (SELECT 1 FROM RequisitionPendingAction pa " +
//            "              WHERE pa.recruitmentRequisitionMaster = r " +
//            "              AND pa.pendingUserId = :userId " +
//            "              AND pa.hasActed = false) " +
//            "   OR EXISTS (SELECT 1 FROM RequisitionGroupAction ga " +
//            "              WHERE ga.recruitmentRequisitionMaster = r " +
//            "              AND ga.actedUserCode = :userId)" +
//            ")")
//    List<RecruitmentRequisitionMaster> findAllByOverralProcessStatusAndUserId(
//            @Param("userId") String userId,
//            @Param("statusList") List<OverallProcessStatus> statusList);

    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
            "LEFT JOIN RequisitionGroupAction ga ON ga.recruitmentRequisitionMaster = r " +
            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
            "AND r.overallProcessStatus IN :statusList " +
            "AND (" +
            "   r.userId = :userId " +
            "   OR EXISTS (SELECT 1 FROM RequisitionApprovedChannel ac " +
            "              WHERE ac.recruitmentRequisitionMaster = r " +
            "              AND ac.memberCode = :userId " +
            "              AND ac.layerPosition = 0) " +
            "   OR EXISTS (SELECT 1 FROM RequisitionPendingAction pa " +
            "              WHERE pa.recruitmentRequisitionMaster = r " +
            "              AND pa.pendingUserId = :userId " +
            "              AND pa.hasActed = false) " +
            "   OR EXISTS (SELECT 1 FROM RequisitionGroupAction ga2 " +
            "              WHERE ga2.recruitmentRequisitionMaster = r " +
            "              AND ga2.actedUserCode = :userId)" +
            ") " +
            "GROUP BY r " +
            "ORDER BY MAX(ga.createdDateTime) DESC NULLS LAST, r.createdDateTime DESC")
    List<RecruitmentRequisitionMaster> findAllByOverralProcessStatusAndUserId(
            @Param("userId") String userId,
            @Param("statusList") List<OverallProcessStatus> statusList);



    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
            "AND (" +
            "   r.userId = :userId " +                                       // 1. Creator
            "   OR EXISTS (SELECT 1 FROM RequisitionApprovedChannel ac " +
            "              WHERE ac.recruitmentRequisitionMaster = r " +
            "              AND ac.memberCode = :userId " +
            "              AND ac.layerPosition = 0) " +                     // 2. Official Raiser (Layer 0)
            "   OR EXISTS (SELECT 1 FROM RequisitionPendingAction pa " +
            "              WHERE pa.recruitmentRequisitionMaster = r " +
            "              AND pa.pendingUserId = :userId " +
            "              AND pa.hasActed = false) " +                      // 3. Current Pending
            "   OR EXISTS (SELECT 1 FROM RequisitionGroupAction ga " +
            "              WHERE ga.recruitmentRequisitionMaster = r " +
            "              AND ga.actedUserCode = :userId)" +                // 4. Previously Acted
            ")")
    Page<RecruitmentRequisitionMaster> findTrackingRequisitionsPaginated(
            @Param("userId") String userId,
            Pageable pageable);

//    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
//            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
//            "AND r.overallProcessStatus IN :statuses " +
//            "ORDER BY r.createdDateTime DESC")
//    List<RecruitmentRequisitionMaster> findAllTrackingRequisitionsByStatuses(
//            @Param("statuses") List<OverallProcessStatus> statuses);
//
//    @Query("""
//    SELECT r FROM RecruitmentRequisitionMaster r
//    LEFT JOIN RequisitionGroupAction ga ON ga.recruitmentRequisitionMaster = r
//    WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE
//    AND r.overallProcessStatus IN :statuses
//    GROUP BY r
//    ORDER BY r.createdDateTime DESC, MAX(ga.createdDateTime) DESC NULLS LAST
//    """)
//    List<RecruitmentRequisitionMaster> findAllTrackingRequisitionsByStatuses(
//            @Param("statuses") List<OverallProcessStatus> statuses
//    );

    @Query("""
    SELECT r FROM RecruitmentRequisitionMaster r 
    LEFT JOIN RequisitionGroupAction ga ON ga.recruitmentRequisitionMaster = r 
    WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE 
    AND r.overallProcessStatus IN :statuses 
    GROUP BY r 
    ORDER BY GREATEST(r.createdDateTime, COALESCE(MAX(ga.createdDateTime), r.createdDateTime)) DESC
    """)
    List<RecruitmentRequisitionMaster> findAllTrackingRequisitionsByStatuses(
            @Param("statuses") List<OverallProcessStatus> statuses
    );


    List<RecruitmentRequisitionMaster> findByCurrentStatusIn(List<RequisitionStatus> statuses);

    List<RecruitmentRequisitionMaster> findByCurrentStatusInAndUserId(List<RequisitionStatus> statuses, String userId);

    long countByCreatedDateBetweenAndRecordStatus(LocalDate start, LocalDate end, RecordStatus status);
    long countByCreatedDateBetween(LocalDate start, LocalDate end);

    long countByCreatedByAndCreatedDateBetweenAndRecordStatus(String userCode, LocalDate start, LocalDate end, RecordStatus status);

    /**
     * Finds unique requisition masters where the user has previously taken an action
     * and the current status is within the provided list.
     */
    @Query("SELECT DISTINCT m FROM RecruitmentRequisitionMaster m " +
            "JOIN RequisitionGroupAction ga ON ga.recruitmentRequisitionMaster = m " +
            "WHERE m.overallProcessStatus IN :statuses " +
            "AND ga.actedUserCode = :userId " +
            "AND m.recordStatus = :recordStatus")
    List<RecruitmentRequisitionMaster> findByOverallProcessStatusInAndActedUser(
            @Param("statuses") List<OverallProcessStatus> statuses,
            @Param("userId") String userId,
            @Param("recordStatus") RecordStatus recordStatus);


    @Query("SELECT r FROM RecruitmentRequisitionMaster r " +
            "WHERE r.recordStatus = com.cd.recruitment_requisition_service.enums.RecordStatus.ACTIVE " +
            "AND EXISTS (SELECT 1 FROM RequisitionRequirementsAllocation ra " +
            "            WHERE ra.recruitmentRequisitionMaster = r " +
            "            AND ra.approvedPlanId = :approvedPlanId)")
    List<RecruitmentRequisitionMaster> findAllRequisitionByApprovedPlanId(@Param("approvedPlanId") String approvedPlanId);

}
