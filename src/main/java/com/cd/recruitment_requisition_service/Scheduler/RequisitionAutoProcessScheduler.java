//package com.cd.recruitment_requisition_service.Scheduler;
//
//import com.cd.recruitment_requisition_service.entity.RecruitmentRequisitionMaster;
//import com.cd.recruitment_requisition_service.entity.RequisitionAction;
//import com.cd.recruitment_requisition_service.entity.RequisitionGroupAction;
//import com.cd.recruitment_requisition_service.entity.RequisitionStatusHistory;
//import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
//import com.cd.recruitment_requisition_service.enums.RequisitionActionType;
//import com.cd.recruitment_requisition_service.enums.RequisitionStatus;
//import com.cd.recruitment_requisition_service.repository.*;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@Slf4j
//public class RequisitionAutoProcessScheduler {
//
//    private final RecruitmentRequisitionMasterRepository requisitionRepository;
//    private final RequisitionActionRepository actionRepository;
//    private final RequisitionStatusHistoryRepository historyRepository;
//    private final RequisitionPendingActionRepository pendingActionRepository;
//    private final RequisitionGroupActionRepository groupActionRepository;
//
//    public RequisitionAutoProcessScheduler(RecruitmentRequisitionMasterRepository requisitionRepository,
//                                           RequisitionActionRepository actionRepository,
//                                           RequisitionStatusHistoryRepository historyRepository,
//                                           RequisitionPendingActionRepository pendingActionRepository,
//                                           RequisitionGroupActionRepository groupActionRepository
//    ) {
//        this.requisitionRepository = requisitionRepository;
//        this.actionRepository = actionRepository;
//        this.historyRepository = historyRepository;
//        this.pendingActionRepository = pendingActionRepository;
//        this.groupActionRepository = groupActionRepository;
//    }
//
//    // -----------------------------------------------------------
//    // 1. AUTO RULE: POSTPONED → AUTO WITHDRAWN (90 DAYS)
//    // -----------------------------------------------------------
//   // @Scheduled(cron = "0 0 1 * * ?")  // Runs daily at 1 AM
//  //  @Scheduled(cron = "0 */5 * * * ?")
//    @Transactional
//    public void autoWithdrawPostponedRequisitions() {
//
//         LocalDateTime cutoffTime = LocalDateTime.now().minusDays(90);
//
//        List<RecruitmentRequisitionMaster> postponedRequisitions =
//                requisitionRepository.findAllByCurrentStatusAndUpdatedAtBefore(
//                        RequisitionStatus.POSTPONED, cutoffTime
//                );
//
//        log.info("[Scheduler:90Days] Found {} POSTPONED plans for auto-withdrawal.", postponedRequisitions.size());
//
//        for (RecruitmentRequisitionMaster requisition : postponedRequisitions) {
//            RequisitionStatus prevStatus = requisition.getCurrentStatus();
//            LocalDateTime now = LocalDateTime.now();
//
//            // 1. Master Update
//            requisition.setCurrentStatus(RequisitionStatus.WITHDRAWN);
//            requisition.setOverallProcessStatus(OverallProcessStatus.COMPLETED);
//            requisition.setUpdatedAt(now);
//            requisitionRepository.save(requisition);
//
//            // 2. Pending Action Cleanup (Removes from "My Desk")
//            pendingActionRepository.deleteAllByRecruitmentRequisitionMasterId(requisition.getId());
//
//            // 3. Group Action Log (For Dashboard Status & Recommendation History)
//            groupActionRepository.save(RequisitionGroupAction.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .actedUserCode("SYSTEM")
//                    .actedUserName("SYSTEM_AUTO")
//                    .layerPosition(requisition.getCurrentLayerPosition() != null ? requisition.getCurrentLayerPosition() : 0)
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .remarks("Auto withdrawn after 90 days in POSTPONED state")
//                  //  .recommendedHeadcount(requisition.getCurrentProposedHeadcount())
//                    .createdDateTime(now)
//                            .createdDate(LocalDate.now())
//                    .build());
//
//            // 4. Status History Log
//            historyRepository.save(RequisitionStatusHistory.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .fromStatus(prevStatus)
//                    .toStatus(RequisitionStatus.WITHDRAWN)
//                    .actedBy("SYSTEM")
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .remarks("Auto withdrawn after 90 days in POSTPONED state")
//                    .actionTime(now)
//                            .createdDateTime(LocalDateTime.now())
//                            .createdDate(LocalDate.now())
//                    .build());
//
//            // 5. Action Log
//            actionRepository.save(RequisitionAction.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .actedBy("SYSTEM")
//                    .actedRole("SYSTEM")
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .previousStatus(prevStatus)
//                    .newStatus(RequisitionStatus.WITHDRAWN)
//                    .remarks("Auto-withdraw by system due to 90-day limit")
//                    .actionSource("SCHEDULER")
//                    .actionDateTime(now)
//                            .createdDateTime(LocalDateTime.now())
//                            .createdDate(LocalDate.now())
//                    .build());
//
//            log.warn("[AUTO-RULE] POSTPONED → WITHDRAWN | Code: {}", requisition.getRequisitionCode());
//        }
//    }
//
//    // -----------------------------------------------------------
//    // 2. AUTO RULE: BACKED → AUTO WITHDRAWN (30 DAYS No Reply)
//    // -----------------------------------------------------------
//   // @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
//  //  @Scheduled(cron = "0 */5 * * * ?")
//    @Transactional
//    public void autoWithdrawBackedRequisitions() {
//        LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(30);
//
//        List<RecruitmentRequisitionMaster> backedRequisitions =
//                requisitionRepository.findAllByCurrentStatusAndUpdatedAtBefore(RequisitionStatus.BACKED, cutoffDateTime);
//
//        log.info("[Scheduler:30Days] Found {} BACKED plans for auto-withdrawal.", backedRequisitions.size());
//
//        for (RecruitmentRequisitionMaster requisition : backedRequisitions) {
//            RequisitionStatus prevStatus = requisition.getCurrentStatus();
//            LocalDateTime now = LocalDateTime.now();
//
//            // 1. Master Update
//            requisition.setCurrentStatus(RequisitionStatus.WITHDRAWN);
//            requisition.setOverallProcessStatus(OverallProcessStatus.COMPLETED);
//            requisition.setUpdatedAt(now);
//            requisitionRepository.save(requisition);
//
//            // 2. Pending Action Cleanup
//            pendingActionRepository.deleteAllByRecruitmentRequisitionMasterId(requisition.getId());
//
//            // 3. Group Action Log
//            groupActionRepository.save(RequisitionGroupAction.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .actedUserCode("SYSTEM")
//                    .actedUserName("SYSTEM_AUTO")
//                    .layerPosition(requisition.getCurrentLayerPosition() != null ? requisition.getCurrentLayerPosition() : 0)
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .remarks("Auto withdrawn - no response within 30 days of send-back")
//                   // .recommendedHeadcount(requisition.getCurrentProposedHeadcount())
//                    .createdDateTime(now)
//                    .build());
//
//            // 4. Status History Log
//            historyRepository.save(RequisitionStatusHistory.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .fromStatus(prevStatus)
//                    .toStatus(RequisitionStatus.WITHDRAWN)
//                    .actedBy("SYSTEM")
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .remarks("Auto withdrawn - no response within 30 days of send-back/info request")
//                    .actionTime(now)
//                            .createdDateTime(now)
//                            .createdDate(LocalDate.now())
//                    .build());
//
//            // 5. Action Log
//            actionRepository.save(RequisitionAction.builder()
//                    .recruitmentRequisitionMaster(requisition)
//                    .actedBy("SYSTEM")
//                    .actedRole("SYSTEM")
//                    .actionType(RequisitionActionType.WITHDRAW)
//                    .previousStatus(prevStatus)
//                    .newStatus(RequisitionStatus.WITHDRAWN)
//                    .remarks("No reply within 30 days of info request/send back")
//                    .actionSource("SCHEDULER")
//                    .actionDateTime(now)
//                            .createdDate(LocalDate.now())
//                            .createdDateTime(LocalDateTime.now())
//                    .build());
//
//            log.warn("[AUTO-RULE] BACKED → WITHDRAWN | Code: {}", requisition.getRequisitionCode());
//        }
//    }
//}
