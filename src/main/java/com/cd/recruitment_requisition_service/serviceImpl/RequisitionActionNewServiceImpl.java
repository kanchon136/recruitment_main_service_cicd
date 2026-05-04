package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.customAnnotation.DatabaseThrottling;
import com.cd.recruitment_requisition_service.dto.RequisitionLayerWrapperDto;
import com.cd.recruitment_requisition_service.dto.RequisitionListViewDto;
import com.cd.recruitment_requisition_service.dto.RequisitionMemberDto;
import com.cd.recruitment_requisition_service.dto.UserRecommendedHeadcountDto;
import com.cd.recruitment_requisition_service.entity.*;
import com.cd.recruitment_requisition_service.enums.*;
import com.cd.recruitment_requisition_service.mapper.EntityAndDtoAllMapper;
import com.cd.recruitment_requisition_service.param.PIDResetParam;
import com.cd.recruitment_requisition_service.param.RequisitionActionParam;
import com.cd.recruitment_requisition_service.repository.*;
import com.cd.recruitment_requisition_service.service.RequisitionActionNewService;
import com.cd.recruitment_requisition_service.utils.ActionFormatterHelper;
import com.cd.recruitment_requisition_service.utils.BaseResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequisitionActionNewServiceImpl implements RequisitionActionNewService {


    private final RecruitmentRequisitionMasterRepository recruitmentRequisitionMasterRepository;
    private final RequisitionApprovedChannelRepository requisitionApprovedChannelRepository;
    private final RequisitionGroupActionRepository requisitionGroupActionRepository;
    private final RequisitionPendingActionRepository requisitionPendingActionRepository; //  NEW
    private final RequisitionStatusHistoryRepository requisitionStatusHistoryRepository;
    private final RequisitionActionRepository requisitionActionRepository; // From old code
    private final RequisitionReplacementPersonRepository replacementPersonRepository;

    public RequisitionActionNewServiceImpl(
            RecruitmentRequisitionMasterRepository recruitmentRequisitionMasterRepository,
            RequisitionApprovedChannelRepository requisitionApprovedChannelRepository,
            RequisitionGroupActionRepository requisitionGroupActionRepository,
            RequisitionPendingActionRepository requisitionPendingActionRepository,
            RequisitionStatusHistoryRepository requisitionStatusHistoryRepository,
            RequisitionActionRepository requisitionActionRepository,
            RequisitionReplacementPersonRepository replacementPersonRepository) {
        this.recruitmentRequisitionMasterRepository = recruitmentRequisitionMasterRepository;
        this.requisitionApprovedChannelRepository = requisitionApprovedChannelRepository;
        this.requisitionGroupActionRepository = requisitionGroupActionRepository;
        this.requisitionPendingActionRepository = requisitionPendingActionRepository;
        this.requisitionStatusHistoryRepository = requisitionStatusHistoryRepository;
        this.requisitionActionRepository = requisitionActionRepository;
        this.replacementPersonRepository = replacementPersonRepository;
    }

    @Transactional
    @Override
    public BaseResponse takeAction(RequisitionActionParam request) {
        BaseResponse baseResponse = new BaseResponse();

        RecruitmentRequisitionMaster requisition = recruitmentRequisitionMasterRepository.findByIdAndRecordStatus(request.getMasterRequisitionId(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Master not found"));

        String actedUserId = request.getActedBy();
        String actedUserName = request.getActedUserName();
        // --- A. DRAFT Submission Logic ---
        if (requisition.getCurrentStatus() == RequisitionStatus.DRAFT) {

            if (request.getActionType() != RequisitionActionType.FORWARD_FOR_RECOMMENDATION) {
                throw new CustomException("Invalid action. DRAFT requisitions must be FORWARD_FOR_RECOMMENDATION.");
            }
             logGroupAction(requisition, actedUserId, actedUserName, request);
             RouteDetails routeDetails = calculateNextRouteDetails(requisition, 0, request);
             logRequisitionAction(requisition, actedUserId, request, RequisitionStatus.SUBMITTED, routeDetails.nextStatus, 0, routeDetails.nextLayer, routeDetails.nextRole);
             routeToNextLayer(requisition, 0, request, routeDetails);
             updateMasterForTransition(requisition, request, routeDetails);
             logStatusHistory(requisition, actedUserId, request, RequisitionStatus.SUBMITTED, routeDetails.nextStatus, routeDetails.nextRole);
             recruitmentRequisitionMasterRepository.save(requisition);
             baseResponse.setMessage("Requisition submitted successfully");
             return baseResponse;
        }

        // --- B. PROCESSING Actions ---
        RequisitionPendingAction pendingAction = requisitionPendingActionRepository
                .findByRecruitmentRequisitionMasterIdAndPendingUserIdAndHasActedFalse(requisition.getId(), actedUserId)
                .orElseThrow(() -> new CustomException("You are not the designated user or have already acted."));

        //  LOGIC CHANGE: If the action is POSTPONE, do NOT set hasActed to true.
        // This keeps the requisition in the current user's "Pending" inbox.
        if (request.getActionType() == RequisitionActionType.HOLD_OFF) {
            pendingAction.setHasActed(false);
            pendingAction.setCreatedDateTime(LocalDateTime.now()); // Update timestamp for sorting
        } else {
            pendingAction.setHasActed(true);
        }
        requisitionPendingActionRepository.save(pendingAction);

        logGroupAction(requisition, actedUserId, actedUserName, request);

        Integer currentLayer = pendingAction.getLayerPosition();

        if (request.getActionType() == RequisitionActionType.HOLD_OFF ||
                request.getActionType() == RequisitionActionType.WITHDRAW) {

            RouteDetails routeDetails = calculateNextRouteDetails(requisition, currentLayer, request);
             // This will call the cleanup logic (Global delete for Withdraw, Return for Postpone)
            routeToNextLayer(requisition, currentLayer, request, routeDetails);

            updateMasterForTransition(requisition, request, routeDetails);
            recruitmentRequisitionMasterRepository.save(requisition);

            baseResponse.setMessage("Requisition has been " + request.getActionType().toString().toLowerCase() + "ed successfully.");
            return baseResponse;
        }


        boolean isLayerComplete = checkLayerCompletion(requisition.getId(), currentLayer);

        // Condition: Layer complete hoyeche OR Final Action (Reject) OR BACK action hoyeche                              was BACK
        if (isLayerComplete || isFinalAction(request.getActionType()) || request.getActionType() == RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION) {

            RouteDetails routeDetails = calculateNextRouteDetails(requisition, currentLayer, request);

            logRequisitionAction(requisition, actedUserId, request, routeDetails.previousStatus, routeDetails.nextStatus, currentLayer, routeDetails.nextLayer, routeDetails.nextRole);

            logStatusHistory(requisition, actedUserId, request, routeDetails.previousStatus, routeDetails.nextStatus, routeDetails.nextRole);

            if (requisition.getOverallProcessStatus() == OverallProcessStatus.PROCESSING) {

                // Ekhane purono pending delete hobe ebong notun layer-e route hobe
                routeToNextLayer(requisition, currentLayer, request, routeDetails);
            }
            // Master status update ebong ReturnToLayer logic ekhane execute hobe
            updateMasterForTransition(requisition, request, routeDetails);
            recruitmentRequisitionMasterRepository.save(requisition);
            // --- NEW LOGIC FOR REPLACEMENT PERSON ---
            // যদি মাস্টার এখন FINAL_APPROVED স্ট্যাটাসে থাকে, তবে রিপ্লেসমেন্ট পারসনদের আপডেট করো
            if (requisition.getCurrentStatus() == RequisitionStatus.FINAL_APPROVED) {
             //   updateRequisiotIDwhenRequisiotnIsFilanalAprved(requisition);
            }
            baseResponse.setMessage("Action taken successfully. Transitioned to " + (routeDetails.nextRole != null ? routeDetails.nextRole : "Next Stage"));
            return baseResponse;
        } else {
            baseResponse.setMessage("Action saved. Waiting for other mandatory members in the current layer.");
            return baseResponse;
        }
    }

    private void updateRequisiotIDwhenRequisiotnIsFilanalAprved(RecruitmentRequisitionMaster master) {

        if (master.getRequisitionType() != null && master.getRequisitionType().name().equalsIgnoreCase("REPLACE")) {

            log.info("Updating Requisition ID for REPLACEMENT persons. Master Code: {}", master.getRequisitionCode());

            // ১. এই রিকুইজিশনের সাথে যুক্ত সব একটিভ রিপ্লেসমেন্ট পারসন খুঁজে বের করা
            List<RequisitionReplacementPerson> replacements = replacementPersonRepository
                    .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, master.getId());

            if (!replacements.isEmpty()) {
                for (RequisitionReplacementPerson person : replacements) {
                    person.setLastRequisitionId(master.getRequisitionCode());
                    person.setUpdatedAt(LocalDateTime.now());
                    person.setUpdatedBy(master.getLastActedBy());
                }

                replacementPersonRepository.saveAll(replacements);
                log.info("Successfully updated requisitionId for {} records.", replacements.size());
            }
        }
    }


     //  Logic to finalize replacement person details upon final approval of the requisition.
    //  It assigns the official Requisition ID and tracks the replacement frequency for the employee.
    private void handleReplacementPersonSync(RecruitmentRequisitionMaster master) {
        // Only process if the requisition type is 'REPLACEMENT'
        if (master.getRequisitionType() != null && master.getRequisitionType().name().equalsIgnoreCase("REPLACE")) {

            log.info("Final approval detected for REPLACEMENT requisition: {}. Syncing replacement person records.", master.getRequisitionCode());

            // Fetch all active replacement persons associated with this master record
            List<RequisitionReplacementPerson> replacements = replacementPersonRepository
                    .findAllByRecordStatusAndRecruitmentRequisitionMaster_id(RecordStatus.ACTIVE, master.getId());

            if (!replacements.isEmpty()) {
                for (RequisitionReplacementPerson person : replacements) {
                    // 1. Calculate how many times this employee has been successfully replaced before.
                   // We count records where 'requisitionId' is not null, signifying a completed process.
                    // 3. Assign the official Requisition Code as the tracking ID
                    person.setLastRequisitionId(master.getRequisitionCode());
                    // 4. Update audit trails
                    person.setUpdatedAt(LocalDateTime.now());
                    person.setUpdatedBy(master.getLastActedBy());
                }
                // Batch save updated records
                replacementPersonRepository.saveAll(replacements);
                log.info("Successfully updated {} replacement records with Requisition ID and Frequency.", replacements.size());
            }
        }
    }

    // DTO to hold dynamic routing calculation results
    private static class RouteDetails {
        public Integer nextLayer;
        public String nextRole;
        public RequisitionStatus previousStatus;
        public RequisitionStatus nextStatus;
        public OverallProcessStatus overallStatus;
    }

    // Calculates the dynamic next layer and role based on business logic.
    private RouteDetails calculateNextRouteDetails(RecruitmentRequisitionMaster requisition,
                                                   Integer currentLayer, RequisitionActionParam request) {

        RouteDetails details = new RouteDetails();
        details.previousStatus = requisition.getCurrentStatus();

        // 1. Final Action Check: NOT_APPROVED
        // Logic: If action is NOT_APPROVED, the process terminates here.
        if (request.getActionType() == RequisitionActionType.NOT_APPROVE) {
            details.nextStatus = RequisitionStatus.NOT_APPROVED;
            details.overallStatus = OverallProcessStatus.NOT_APPROVED;
            details.nextLayer = null;
            details.nextRole = null;
            return details;
        }
        // 2. Final Action Check: WITHDRAW
        if (request.getActionType() == RequisitionActionType.WITHDRAW) {
            details.nextStatus = RequisitionStatus.WITHDRAWN;
            details.overallStatus = OverallProcessStatus.WITHDRAWN;  // was Completed
            details.nextLayer = null;
            details.nextRole = null;
            return details;
        }
        // 3. Initial Status Determination (Based on ActionType)
        details.nextStatus = determineNextStatus(request.getActionType());
        details.overallStatus = determineOverallStatus(details.nextStatus);

        // 4. Temporary Pause: HOLD_OFF (Legacy: POSTPONE)
        if (request.getActionType() == RequisitionActionType.HOLD_OFF) {
            details.nextStatus = RequisitionStatus.POSTPONED;
            details.nextLayer = currentLayer;
            details.nextRole = request.getActedBy();
            return details;
        }
        // --- Start of Specific Termination Rules (Similar to NOT_RECOMMEND) ---
        // 5. Special Rule: NOT_RECOMMEND
        if (request.getActionType() == RequisitionActionType.NOT_RECOMMEND) {
            details.nextStatus = RequisitionStatus.NOT_RECOMMENDED;
            details.nextLayer = (request.getTargetLayerPosition() != null)
                    ? request.getTargetLayerPosition() : currentLayer;
            details.nextRole = null;
            details.overallStatus = OverallProcessStatus.NOT_RECOMMENDED;
            return details;
        }
        // 6. Special Rule: NOT_ANALYSIS
        if (request.getActionType() == RequisitionActionType.NOT_ANALYSIS) {
            details.nextStatus = RequisitionStatus.NOT_ANALYZED;
            details.nextLayer = (request.getTargetLayerPosition() != null)
                    ? request.getTargetLayerPosition() : currentLayer;
            details.nextRole = null;
            details.overallStatus = OverallProcessStatus.NOT_ANALYZED;
            return details;
        }
        // 7. Special Rule: NOT_VERIFY
        if (request.getActionType() == RequisitionActionType.NOT_VERIFY) {
            details.nextStatus = RequisitionStatus.NOT_VERIFIED;
            details.nextLayer = (request.getTargetLayerPosition() != null)
                    ? request.getTargetLayerPosition() : currentLayer;
            details.nextRole = null;
            details.overallStatus = OverallProcessStatus.NOT_VERIFIED;
            return details;
        }
        // --- End of Specific Termination Rules ---
        int nextLayer;
        String nextRole = null;
        // --- 8. Dynamic Routing Logic (Forward/Backward) ---
        // A. BACKWARD Logic (Back / Info Request / Send Back)
        if (request.getActionType() == RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION ||
                request.getActionType() == RequisitionActionType.BACK) {

            nextLayer = (request.getTargetLayerPosition() != null) ? request.getTargetLayerPosition() : currentLayer - 1;

            if (nextLayer < 0) nextLayer = 0;
            nextRole = request.getSelectedNextUserId();
            details.nextStatus = RequisitionStatus.BACKED;
        }
        // B. FORWARD Logic
        else {
            // Priority 1: Manual User Selection (Re-forward)
            if (request.getSelectedNextUserId() != null && !request.getSelectedNextUserId().isEmpty()) {
                nextLayer = (request.getTargetLayerPosition() != null) ?
                        request.getTargetLayerPosition() : currentLayer + 1;
                nextRole = request.getSelectedNextUserId();

                if (requisition.getCurrentStatus() == RequisitionStatus.BACKED) {
                    details.nextStatus = RequisitionStatus.UNDER_REVIEW;
                } else {
                    details.nextStatus = determineNextStatus(request.getActionType());
                }
            } else {
                // Priority 2: Automatic Multi-jump (Based on action history)
                Integer jumpTargetLayer = findSpecificSenderLayer(requisition.getId(), currentLayer, request.getActedBy());

                if (jumpTargetLayer != null && jumpTargetLayer > currentLayer) {
                    nextLayer = jumpTargetLayer;
                    nextRole = findSpecificSenderUser(requisition.getId(), currentLayer, request.getActedBy());
                    details.nextStatus = RequisitionStatus.UNDER_REVIEW;
                } else {
                    // Priority 3: Standard Sequence (Linear flow)
                    nextLayer = (request.getTargetLayerPosition() != null) ?
                            request.getTargetLayerPosition() : currentLayer + 1;
                    details.nextStatus = determineNextStatus(request.getActionType());
                }
            }
        }
        // 9. --- URS BUSINESS LOGIC: FINANCE_HOD Skip (Business Unit Check) ---
        List<RequisitionApprovedChannel> nextChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(requisition.getId(), nextLayer)
                .stream().filter(f-> f.getRecordStatus()== RecordStatus.ACTIVE).toList();

        if (!nextChannels.isEmpty()) {
            boolean shouldSkipFinance = nextChannels.stream().anyMatch(channel ->
                    channel.getPanelMember() != null && channel.getPanelMember().contains("FINANCE_HOD") &&
                            requisition.getBusinessUnit() != null && !requisition.getBusinessUnit().equals("SALES_AND_DISTRIBUTION")
            );

            if (shouldSkipFinance) {
                nextLayer += 1;
                nextChannels = requisitionApprovedChannelRepository
                        .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(requisition.getId(), nextLayer);
            }
        }
        // 10. --- Final Routing Assignment ---
        if (nextChannels.isEmpty()) {
            details.overallStatus = OverallProcessStatus.COMPLETED;
            details.nextStatus = RequisitionStatus.FINAL_APPROVED;
            details.nextLayer = null;
            details.nextRole = null;
        } else {
            details.nextLayer = nextLayer;
            // Priority: Manual selection/Jump role > First member in the channel
            details.nextRole = (nextRole != null) ? nextRole :
                    (request.getSelectedNextUserId() != null ? request.getSelectedNextUserId() : nextChannels.get(0).getMemberCode());
        }

        return details;
    }


    private void updateMasterForTransition(RecruitmentRequisitionMaster requisition, RequisitionActionParam request, RouteDetails routeDetails) {

        // Set Process Initiation Date (PID) only on Final Approval
        if (request.getActionType() == RequisitionActionType.FINAL_APPROVE) {
            requisition.setProcessInitiationDate(LocalDate.now());
        }

        if (request.getActionType() != RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION &&
                requisition.getReturnToLayer() != null && // it is not used to return logic
                routeDetails.nextLayer != null &&
                routeDetails.nextLayer.equals(requisition.getReturnToLayer())) {

            requisition.setReturnToLayer(null);
        }
         //  REMOVED: The previous 'isBackTrack' block that was mistakenly
        //   nullifying returnToLayer during the BACK action itself.
        // Update tracking fields
        requisition.setLastActedBy(request.getActedBy());
        requisition.setLastActedRole(request.getActedRole());
        requisition.setLastActionRemarks(request.getRemarks());
        requisition.setLastActionType(request.getActionType());
        requisition.setUpdatedAt(LocalDateTime.now());

        // Update process status and routing info
        requisition.setOverallProcessStatus(routeDetails.overallStatus);
        requisition.setCurrentStatus(routeDetails.nextStatus);
        requisition.setCurrentRole(request.getActedRole());
        requisition.setNextRole(routeDetails.nextRole);
        requisition.setCurrentLayerPosition(routeDetails.nextLayer);
    }

    // Helper method to clean up old pending actions and create new ones for the next layer.
    private void routeToNextLayer(RecruitmentRequisitionMaster requisition, Integer currentLayer, RequisitionActionParam request, RouteDetails routeDetails) {
        // পোস্টপোন হলে কোনো মুভমেন্ট নেই, তাই রিটার্ন
        if (request.getActionType() == RequisitionActionType.HOLD_OFF) {
            return;
        }
        //  RouteDetails routeDetails = calculateNextRouteDetails(requisition, currentLayer, request);
        if (request.getActionType() == RequisitionActionType.WITHDRAW) {
            requisition.setCurrentStatus(RequisitionStatus.WITHDRAWN);
          //  requisition.setOverallProcessStatus(OverallProcessStatus.COMPLETED);
            requisition.setOverallProcessStatus(OverallProcessStatus.WITHDRAWN);
            requisitionPendingActionRepository.deleteAllByRecruitmentRequisitionMasterId(requisition.getId());
            return;
        }
        // মাস্টার টেবিল আপডেট (nextRole এবং nextLayer এখন জাম্প লজিক সাপোর্ট করে)
        requisition.setCurrentStatus(routeDetails.nextStatus);
        requisition.setOverallProcessStatus(routeDetails.overallStatus);
        requisition.setNextRole(routeDetails.nextRole);
        requisition.setCurrentLayerPosition(routeDetails.nextLayer);
        // 3. Determine Back-track status (Includes new Role-specific rejections)
        boolean isBackTrack =
                routeDetails.nextStatus == RequisitionStatus.INFO_REQUESTED ||
                        routeDetails.nextStatus == RequisitionStatus.BACKED ||
                        routeDetails.nextStatus == RequisitionStatus.NOT_RECOMMENDED ||
                        routeDetails.nextStatus == RequisitionStatus.NOT_ANALYZED ||
                        routeDetails.nextStatus == RequisitionStatus.NOT_VERIFIED;

        // ৪. বর্তমান ইউজারের রেকর্ড হ্যান্ডেল করা
        if (isBackTrack) {
            // শুধুমাত্র ব্যাক করলে বর্তমান ইউজারের পেন্ডিং রেকর্ড ডিলিট হবে।
            // ফরওয়ার্ডের ক্ষেত্রে takeAction মেথড অলরেডি hasActed=true করে দিয়েছে।
            requisitionPendingActionRepository.deleteByRecruitmentRequisitionMasterIdAndLayerPositionAndPendingUserId(
                    requisition.getId(), currentLayer, request.getActedBy());
        }
        // যদি প্রসেস শেষ হয়ে যায় (Final Approved) তবে আর টার্গেট রেকর্ড লাগবে না
        if (routeDetails.overallStatus != OverallProcessStatus.PROCESSING) {
            return;
        }
        // ৫. টার্গেট ইউজারের (Receiver) রেকর্ড হ্যান্ডেল করা (জাম্প ফরোয়ার্ড সাপোর্ট সহ)
        Optional<RequisitionPendingAction> existingTarget = requisitionPendingActionRepository
                .findByRecruitmentRequisitionMasterIdAndLayerPositionAndPendingUserId(
                        requisition.getId(), routeDetails.nextLayer, routeDetails.nextRole);

        if (existingTarget.isPresent()) {
            // ব্যাক সাইকেল বা জাম্প ফরোয়ার্ড সাইকেল - পুরনো রেকর্ডটি রি-অ্যাক্টিভ করা
            RequisitionPendingAction pending = existingTarget.get();
            pending.setHasActed(false);
            pending.setCreatedDateTime(LocalDateTime.now());
            requisitionPendingActionRepository.save(pending);
        } else {
            // সাধারণ ফ্লো - নতুন রেকর্ড তৈরি
            RequisitionApprovedChannel nextChannel = requisitionApprovedChannelRepository
                    .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(requisition.getId(), routeDetails.nextLayer)
                    .stream()
                    .filter(c -> c.getMemberCode().equals(routeDetails.nextRole))
                    .findFirst()
                    .orElseThrow(() -> new CustomException("Next user channel setup missing for user: " + routeDetails.nextRole));

            RequisitionPendingAction newPendingAction = RequisitionPendingAction.builder()
                    .recruitmentRequisitionMaster(requisition)
                    .pendingUserId(nextChannel.getMemberCode())
                    .pendingUserName(nextChannel.getPanelMember())
                    .layerPosition(nextChannel.getLayerPosition())
                    .isMandatory(nextChannel.getIsMandatoryAction())
                    .hasActed(false)
                    .createdDateTime(LocalDateTime.now())
                    .build();

            requisitionPendingActionRepository.save(newPendingAction);
        }
    }

    @Transactional
    private void logGroupAction(RecruitmentRequisitionMaster requisition, String actedUserId,
                                String actedUserName, RequisitionActionParam request
    ) {

        Integer currentLayer = requisition.getCurrentLayerPosition();

        RequisitionApprovedChannel requisitionApprovedChannel =
                requisitionApprovedChannelRepository.findAllByRecruitmentRequisitionMasterId(requisition.getId())
                        .stream().filter(f -> Objects.equals(f.getLayerPosition(), currentLayer))
                        .findFirst().orElseThrow(() -> new CustomException("ApprovalChanel not found  by layerPosition , when logGroupAction"));

        RequisitionGroupAction groupAction =
                RequisitionGroupAction.builder()
                        .recruitmentRequisitionMaster(requisition)
                        .actionType(request.getActionType())
                        .currentRole(requisitionApprovedChannel.getAuthorizationType().name())
                        .remarks(request.getRemarks())
                        //  .remarks(request.getActionType().name())
                        .actedUserCode(actedUserId)
                        .actedUserName(actedUserName)
                        .recommendedHeadcount(request.getRecommendedHeadcount())
                        .createdDate(LocalDate.now())
                        .createdDateTime(LocalDateTime.now())
                        // .layerPosition(request.getTargetLayerPosition())
                        .layerPosition(requisition.getCurrentLayerPosition())
                        .build();

        if (request.getRecommendedAllocationsParams() != null
                && !request.getRecommendedAllocationsParams().isEmpty()) {

            List<RecommendedAllocations> allocations =
                    request.getRecommendedAllocationsParams()
                            .stream()
                            .<RecommendedAllocations>map(param ->
                                    RecommendedAllocations.builder()
                                            .requisitionGroupAction(groupAction)
                                            .recruitmentRequisitionMaster(requisition)
                                            .approvedPlanId(param.getApprovedPlanId())
                                            .lastPlacementDate(param.getLastPlacementDate())
                                            .shiftAllocationCode(param.getShiftAllocationCode())
                                            .shiftAllocationName(param.getShiftAllocationName())
                                            .recomendedHeadCount(param.getRecomendedHeadCount())
                                            .personalSubAreaCode(param.getPersonalSubAreaCode())
                                            .personalSubAreaName(param.getPersonalSubAreaName())
                                            .orgaUnitCode(param.getOrgaUnitCode())
                                            .orgaUnitName(param.getOrgaUnitName())
                                            .anyPlanApproved(param.isAnyPlanApproved())
                                            .createdDate(LocalDate.now())
                                            .createdDateTime(LocalDateTime.now())
                                            .createdBy(param.getActedUserCode())
                                            .actedUserName(param.getActedUserName())
                                            .build()
                                            ).toList();

                     groupAction.setRecommendedAllocations(allocations);
                }
        requisitionGroupActionRepository.save(groupAction);
        }


    private void logRequisitionAction(RecruitmentRequisitionMaster requisition, String actedUserId, RequisitionActionParam request,
                                      RequisitionStatus previousStatus, RequisitionStatus nextStatus, Integer fromLayer, Integer toLayer, String nextRole) {
        // This log requires finding a representative channel, which can be tricky in parallel flow.
        // We'll use the first one found for the 'from' layer if available.
        Optional<RequisitionApprovedChannel> channelOpt = requisitionApprovedChannelRepository
                .findTopByRecruitmentRequisitionMasterIdAndLayerPosition(requisition.getId(), fromLayer);

        RequisitionAction action = RequisitionAction.builder()
                .recruitmentRequisitionMaster(requisition)
                .approvedChannel(channelOpt.orElse(null))
                .actedBy(request.getActedBy())
                .actedRole(request.getActedRole())
                .actionType(request.getActionType())
                .remarks(request.getRemarks())
                .previousStatus(previousStatus)
                .newStatus(nextStatus)
                .fromLayerPosition(fromLayer)
                .toLayerPosition(toLayer)
                .toStage(nextRole)
                .actionSource("WEB")
                .actionDateTime(LocalDateTime.now())
                .createdDate(LocalDate.now())
                .createdDateTime(LocalDateTime.now())
                .build();
        requisitionActionRepository.save(action);
    }

    private void logStatusHistory(RecruitmentRequisitionMaster requisition, String actedUserId, RequisitionActionParam request,
                                  RequisitionStatus previousStatus, RequisitionStatus nextStatus, String nextRole) {
        RequisitionStatusHistory history = RequisitionStatusHistory.builder()
                .recruitmentRequisitionMaster(requisition)
                .fromStatus(previousStatus)
                .toStatus(nextStatus)
                .fromRole(request.getActedRole())
                .toRole(nextRole)
                .actedBy(request.getActedBy())
                .actionType(request.getActionType())
                .remarks(request.getRemarks())
                .actionTime(LocalDateTime.now())
                .createdDate(LocalDate.now())
                .createdDateTime(LocalDateTime.now())
                .build();
        requisitionStatusHistoryRepository.save(history);
    }


    private RequisitionStatus determineNextStatus(RequisitionActionType actionType) {

        return switch (actionType) {
            // --- Core Actions (Matrix: Withdraw & Hold off) ---
            case SUBMIT -> RequisitionStatus.SUBMITTED;
            case WITHDRAW -> RequisitionStatus.WITHDRAWN;
            case HOLD_OFF, POSTPONE -> RequisitionStatus.POSTPONED;
            case FORWARD_FOR_RECOMMENDATION -> RequisitionStatus.UNDER_COORDINATION;
            case FORWARD_FOR_FURTHER_REVIEW -> RequisitionStatus.UNDER_REVIEW;
            case FORWARD_AGAIN -> RequisitionStatus.UNDER_REVIEW;
            case VERIFY -> RequisitionStatus.VERIFIED;
            case ANALYSIS -> RequisitionStatus.ANALYZED;
            case RECOMMEND -> RequisitionStatus.RECOMMENDED;
            case COORDINATE -> RequisitionStatus.COORDINATED;
            case NOT_RECOMMEND -> RequisitionStatus.NOT_RECOMMENDED;
            case NOT_APPROVE -> RequisitionStatus.NOT_APPROVED;
            case NOT_ANALYSIS -> RequisitionStatus.NOT_ANALYZED;
            case NOT_VERIFY -> RequisitionStatus.NOT_VERIFIED;
            case REJECT -> RequisitionStatus.REJECTED;
            case SEND_BACK_FOR_FURTHER_INFO_ACTION, BACK -> RequisitionStatus.BACKED;
            case APPROVE, FINAL_APPROVE -> RequisitionStatus.FINAL_APPROVED;

            default -> {
                try {
                    yield RequisitionStatus.valueOf(actionType.name());
                } catch (IllegalArgumentException e) {
                    yield RequisitionStatus.UNDER_REVIEW;
                }
            }
        };
    }

    private OverallProcessStatus determineOverallStatus(RequisitionStatus status) {
        if (status == null) {
            return OverallProcessStatus.DRAFT;
        }
        return switch (status) {
            // 1. Terminal Statuses
            case FINAL_APPROVED -> OverallProcessStatus.COMPLETED;
            case REJECTED -> OverallProcessStatus.REJECTED;
            case WITHDRAWN -> OverallProcessStatus.WITHDRAWN;
            // 2. Initial Status
            case DRAFT -> OverallProcessStatus.DRAFT;
            // 3. Active Processing Statuses (Based on Matrix & Business Logic)
            // Everything else remains in the "In-Progress" pool to maintain visibility
            case SUBMITTED,
                 UNDER_REVIEW,
                 UNDER_VERIFICATION,
                 VERIFIED,
                 UNDER_ANALYSIS,
                 ANALYZED,
                 UNDER_RECOMMENDATION,
                 RECOMMENDED,
                 NOT_RECOMMENDED,
                 UNDER_COORDINATION,
                 COORDINATED,
                 UNDER_APPROVAL,
                 NOT_APPROVED,
                 NOT_ANALYZED,
                 NOT_VERIFIED,
                 BACKED,
                 INFO_REQUESTED,
                 POSTPONED -> OverallProcessStatus.PROCESSING;
                 default -> OverallProcessStatus.PROCESSING;
        };
    }

    // 2. LISTING LOGIC: getRequisitionsForUser (Dashboard/My Desk)
    @Override
    @DatabaseThrottling
    public List<RequisitionListViewDto> getRequisitionsForUser(String userId, String filterType) {

        List<RecruitmentRequisitionMaster> masterList = recruitmentRequisitionMasterRepository.findTrackingRequisitions(userId);

        List<RequisitionListViewDto> dtoList = masterList.stream()
                .map(master -> mapMasterToListViewDto(master, userId)).toList();

        return switch (filterType.toUpperCase().replace(" ", "_")) {
            case "MY_DESK" -> dtoList.stream().filter(RequisitionListViewDto::isActionRequiredByMe).toList();

            case "IN_PROCESS" -> dtoList.stream()
                    .filter(dto -> dto.getOverallProcessStatus() == OverallProcessStatus.PROCESSING)
                    // নতুন লজিক: যদি আমার কাছে অ্যাকশন পেন্ডিং থাকে (My Desk-এ থাকে), তবে এখানে দেখাবে না
                    .filter(dto -> !dto.isActionRequiredByMe())  .toList();
            case "APPROVED" ->
                // এখানে শুধু তারাই আসবে যারা সফলভাবে FINAL_APPROVED হয়েছে
                    dtoList.stream().filter(dto -> dto.getOverallProcessStatus() == OverallProcessStatus.COMPLETED
                                    && "FINAL_APPROVED".equalsIgnoreCase(dto.getCurrentStatus())) .toList();
            case "NOT_APPROVED" ->
                // আপনার নতুন লজিক অনুযায়ী: যারা NOT_APPROVED তারা REJECTED ট্যাবে দেখাবে
                    dtoList.stream().filter(dto -> "NOT_APPROVED".equalsIgnoreCase(dto.getCurrentStatus())).toList();

            case "WITHDRAWN" ->
                    dtoList.stream().filter(dto -> dto.getOverallProcessStatus() == OverallProcessStatus.WITHDRAWN).toList();

            case "NOT_RECOMMENDED" ->
                    dtoList.stream().filter(dto -> dto.getOverallProcessStatus() == OverallProcessStatus.NOT_RECOMMENDED) .toList();

            default -> Collections.emptyList();
        };
    }

    @Override
    public List<RequisitionListViewDto> findAllRequisitionByOverralStatusAndUserID(String userId, List<OverallProcessStatus> overallProcessStatusList) {
        List<RecruitmentRequisitionMaster> masterList = recruitmentRequisitionMasterRepository.findAllByOverralProcessStatusAndUserId(userId, overallProcessStatusList);

        return masterList.stream()
                .map(master -> mapMasterToListViewDto(master, userId)).toList();

    }

    @Override
    public List<RequisitionListViewDto> getAdminTracking(List<OverallProcessStatus> statusList) {

        List<RecruitmentRequisitionMaster> allRequisitions = recruitmentRequisitionMasterRepository.findAllTrackingRequisitionsByStatuses(statusList);
        if (allRequisitions.isEmpty()) {
            return Collections.emptyList();
        }

        return allRequisitions.stream()
                .map(master -> {
                    // master.getOverallProcessStatus() যদি DRAFT হয়,
                    return mapMasterToListViewDto(master, "");
                })
                .collect(Collectors.toList());
    }

    // Helper Method: Checks if all mandatory users in the current layer have acted. for system
    // Layer completion validation new for manual
    private boolean checkLayerCompletion(Long masterId, Integer layer) {
        return requisitionPendingActionRepository.findAllByRecruitmentRequisitionMasterIdAndLayerPositionAndIsMandatoryTrue
                (masterId, layer).stream().allMatch(RequisitionPendingAction::isHasActed);
    }

    // Helper Method: Checks if the action type results in process completion or termination.
    private boolean isFinalAction(RequisitionActionType actionType) {
        return actionType == RequisitionActionType.REJECT
                || actionType == RequisitionActionType.FINAL_APPROVE;
    }

    private RequisitionListViewDto mapMasterToListViewForApprovedPlanIdDto(RecruitmentRequisitionMaster master) {

        RequisitionListViewDto dto = new RequisitionListViewDto();
        Long mId = master.getId();

        // 1. Get all unique layers defined for this requisition
        List<RequisitionApprovedChannel> allChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterId(mId).stream()
                .filter(f-> f.getRecordStatus() == RecordStatus.ACTIVE).toList();

        RequisitionApprovedChannel raiserChannel = allChannels.stream()
                .filter(c -> c.getLayerPosition() != null && c.getLayerPosition() == 0)
                .findFirst()
                .orElse(null);

        // Basic Master Data (Master Table) ---
        dto.setId(mId);
        dto.setRequisitionCode(master.getRequisitionCode());
        dto.setOverallProcessStatus(master.getOverallProcessStatus());
        dto.setBusinessUnit(master.getBusinessUnit());
        dto.setCurrentLayer(master.getCurrentLayerPosition());
        dto.setPositionName(master.getPositionName());

        dto.setPersonalAreaCode(master.getPersonalAreaCode());
        dto.setPersonalAreaName(master.getPersonalAreaName());
        dto.setWorkplaceCode(master.getWorkplaceCode());
        dto.setWorkplaceName(master.getWorkplaceName());
        dto.setEmployeeCategoryCode(master.getEmployeeCategoryCode());
        dto.setEmployeeCategoryName(master.getEmployeeCategoryName());
        dto.setCompanyCode(master.getCompanyCode());
        dto.setCompanyName(master.getCompanyName());
        dto.setEmployeeSubGroupCode(master.getEmployeeSubGroupCode());
        dto.setEmployeeSubGroupName(master.getEmployeeSubGroupName());
        dto.setCurrentRole(master.getLastActionType() != null ? master.getLastActionType().name() : null);
        dto.setPositionCode(master.getPositionCode());
        dto.setProcessInitiationDate(master.getProcessInitiationDate());
        dto.setCurrentStatus(master.getCurrentStatus().name());
        dto.setRequisitionType(master.getRequisitionType());
        dto.setReportingTo(master.getReportingTo());

        // --- 2. Raiser Info ---
        if (raiserChannel != null) {
            dto.setRaiserUserId(raiserChannel.getMemberCode());
            dto.setRaiserUsername(raiserChannel.getPanelMember());
        } else {
            dto.setRaiserUserId(master.getUserId());
            dto.setRaiserUsername(master.getUserName());
        }

        // --- 3. Initial Proposed Headcount & Allocations ---
        if (master.getAllocations() != null && !master.getAllocations().isEmpty()) {
            master.getAllocations().stream().findFirst()
                    .ifPresent(present -> dto.setAnyPlanApproved(present.isAnyPlanApproved()));

            int totalProposedHeadcount = master.getAllocations().stream()
                    .filter(f -> f.getRecordStatus() == RecordStatus.ACTIVE)
                    .mapToInt(alloc -> alloc.getNoOfRequirements() != null ? alloc.getNoOfRequirements() : 0)
                    .sum();
            dto.setInitialProposedHeadcount(totalProposedHeadcount);

            dto.setRequisitionAllocations(master.getAllocations().stream()
                    .map(EntityAndDtoAllMapper::allocationEntityToDto).collect(Collectors.toList()));
        } else {
            dto.setRequisitionAllocations(Collections.emptyList());
        }
        // --- 4. PROGRESS CALCULATIONS (Core Logic) ---
        // রাইসার বাদে শুধুমাত্র অ্যাপ্রুভাল লেয়ারগুলো নিন (X of 4 এর '4')
        List<Integer> approvalLayers = allChannels.stream()
                .map(RequisitionApprovedChannel::getLayerPosition)
                .filter(l -> l != null && l > 0)
                .distinct()
                .sorted()
                .toList();

        dto.setTotalApprovalStages(approvalLayers.size());
        // হিস্ট্রি এবং পেন্ডিং ডাটা নিয়ে আসা
        List<RequisitionGroupAction> allGroupActions = requisitionGroupActionRepository
                .findAllByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeAsc(mId);
        List<RequisitionPendingAction> pendingActions = requisitionPendingActionRepository
                .findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(mId);
        // বর্তমানে ফাইলটি যে লেয়ারে পেন্ডিং আছে
        Integer currentPendingLayer = pendingActions.stream()
                .map(RequisitionPendingAction::getLayerPosition)
                .min(Integer::compare)
                .orElse(null);
        // *** এই অংশটুকু পরিবর্তন করুন (UNIQUE ACTED LAYERS) ***
        // আমরা শুধু সেই লেয়ারগুলো নেব যারা "ফরওয়ার্ড" বা "অ্যাপ্রুভ" করেছে, "ব্যাক" করেছে এমন লেয়ার নয়।
        Set<Integer> uniqueActedLayers = allGroupActions.stream()
                .filter(ga -> ga.getLayerPosition() != null && ga.getLayerPosition() > 0)
                // যারা ব্যাক করেছে তাদের বাদ দিন
                .filter(ga -> ga.getActionType() != RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION
                        && ga.getActionType() != RequisitionActionType.BACK
                        && ga.getActionType() !=  RequisitionActionType.HOLD_OFF)
                .map(RequisitionGroupAction::getLayerPosition)
                .collect(Collectors.toSet());

        // স্কিপড লেয়ারগুলো সংগ্রহ
        Set<Integer> skippedLayers = allChannels.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsSkiped()) && c.getLayerPosition() != null && c.getLayerPosition() > 0)
                .map(RequisitionApprovedChannel::getLayerPosition)
                .collect(Collectors.toSet());

        int completedStagesCount = 0;

        if (master.getOverallProcessStatus() == OverallProcessStatus.COMPLETED) {
            completedStagesCount = approvalLayers.size();
        } else {
            // লজিক: সম্পন্ন লেয়ার = (যারা সফলভাবে পাস করেছে + যারা স্কিপড হয়েছে)
            Set<Integer> allFinishedLayers = new HashSet<>(uniqueActedLayers);
            allFinishedLayers.addAll(skippedLayers);

            // ফাইল এখন যার ডেস্কে পেন্ডিং, সেই লেয়ারটি 'Completed' থেকে বাদ যাবে
            if (currentPendingLayer != null) {
                allFinishedLayers.remove(currentPendingLayer);
            }

            completedStagesCount = allFinishedLayers.size();
        }

        dto.setTotalCompletedStages(Math.min(completedStagesCount, approvalLayers.size()));
        dto.setTotalStageSkippedCount(skippedLayers.size());

        // --- 3. Current Proposed Status & User Recommendations (Group Action Table) ---
        // BUSINESS LOGIC: Get the latest recommended headcount as "Proposal Current Status"
        allGroupActions.stream()
                .reduce((first, second) -> second) //
                .ifPresentOrElse(lastGa -> {
                    // Set the name of the user who took the latest action
                    dto.setLastActedUserName(lastGa.getActedUserName());
                    dto.setLastActedUserId(lastGa.getActedUserCode());
                    //dto.setLastMemberOpinion(lastGa.getRemarks());
                    dto.setLastMemberOpinion(ActionFormatterHelper.getReportActionName(lastGa.getActionType().name()));
                    if (lastGa.getRecommendedAllocations() != null) {
                        dto.setRecommendedAllocations(lastGa.getRecommendedAllocations());
                    } else dto.setRequisitionAllocations(Collections.emptyList());
                    // Set the latest recommended headcount if it is provided
                    if (lastGa.getRecommendedHeadcount() != null) {
                        dto.setCurrentProposedHeadcount(lastGa.getRecommendedHeadcount());
                    }
                }, () -> {
                    // Default to the original raiser if no actions have been recorded yet
                    dto.setLastActedUserName(master.getUserName());
                });

        dto.setUserWiseRecommendations(allGroupActions.stream().map(action -> {

            UserRecommendedHeadcountDto recDto = new UserRecommendedHeadcountDto();
            recDto.setActedUserId(action.getActedUserCode());
            recDto.setActedUserName(action.getActedUserName());
            recDto.setHeadcount(action.getRecommendedHeadcount());
            return recDto;
        }).collect(Collectors.toList()));

        // --- 4. Pending Action & Current Desk tracking ---
        List<RequisitionPendingAction> currentPendings = requisitionPendingActionRepository
                .findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(mId);

        String deskInfo = "";

        if (master.getCurrentStatus() == RequisitionStatus.DRAFT) {
            deskInfo = (raiserChannel != null) ? raiserChannel.getPanelMember() : master.getUserName();
        } else {
            // ১. প্রসেসিং থাকলে বর্তমান পেন্ডিং ইউজারদের দেখাবে
            deskInfo = currentPendings.stream()
                    .map(RequisitionPendingAction::getPendingUserName)
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .collect(Collectors.joining(", "));

            // ২. যদি পেন্ডিং না থাকে (প্রসেস ক্লোজড), তবে শেষ কে অ্যাকশন নিয়েছে তার নাম দেখাবে
            if (deskInfo.isEmpty()) {
                // হিস্ট্রি থেকে সর্বশেষ অ্যাকশনটি খুঁজে বের করা
                String lastActorName = allGroupActions.stream()
                        .reduce((first, second) -> second) // সর্বশেষ রেকর্ড
                        .map(RequisitionGroupAction::getActedUserName)
                        .orElse("N/A");

                if (master.getCurrentStatus() == RequisitionStatus.NOT_APPROVED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getCurrentStatus() == RequisitionStatus.FINAL_APPROVED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getOverallProcessStatus() == OverallProcessStatus.NOT_RECOMMENDED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getOverallProcessStatus() == OverallProcessStatus.WITHDRAWN) {
                    deskInfo = "WITHDRAWN";
                    deskInfo = "ARCHIVED";
                } else {
                    deskInfo = "CLOSED";
                    deskInfo = "ARCHIVED";
                }
            }
        }
        dto.setCurrentDesk(deskInfo);

        dto.setCurrentPendingUsers(currentPendings.stream()
                .map(p -> p.getPendingUserId().toString())
                .collect(Collectors.joining(", ")));

        dto.setMandatoryActionRemaining(currentPendings.stream().anyMatch(RequisitionPendingAction::getIsMandatory));

        // --- 5. Last Movement & Opinion Tracking (Requisition Action Table) ---
        requisitionActionRepository.findTopByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDesc(mId)
                .ifPresentOrElse(lastAction -> {
                    dto.setLastTransitionType(lastAction.getActionType().name());
                    dto.setLastTransitionRole(lastAction.getActedRole());
                    dto.setLastMovementDate(lastAction.getActionDateTime());
                    dto.setLastActionRemarks(lastAction.getRemarks());

                    // NEW FIELDS MAPPING:
                    //  dto.setLastMemberOpinion(lastAction.getRemarks()); // Last panel member's opinion
                    dto.setApprovalStageCompleted(lastAction.getActedRole()); // Completed role/stage
                }, () -> {
                    dto.setLastTransitionType("INITIAL");
                    dto.setLastTransitionRole("RAISER");
                  //  dto.setLastMovementDate(master.getCreatedDateTime());
                    dto.setLastActionRemarks("");
                    dto.setApprovalStageCompleted("RAISER");
                });

        return dto;
    }


    // Helper Method: Maps Master Entity to DTO and calculates derived fields.
    private RequisitionListViewDto mapMasterToListViewDto(RecruitmentRequisitionMaster master, String loggedInUserId) {

        RequisitionListViewDto dto = new RequisitionListViewDto();
        Long mId = master.getId();

        // 1. Get all unique layers defined for this requisition
        List<RequisitionApprovedChannel> allChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterId(mId).stream()
                .filter(f-> f.getRecordStatus() == RecordStatus.ACTIVE).toList();

        RequisitionApprovedChannel raiserChannel = allChannels.stream()
                .filter(c -> c.getLayerPosition() != null && c.getLayerPosition() == 0)
                .findFirst()
                .orElse(null);

        // --- 1. Basic Master Data (Master Table) ---
        dto.setId(mId);
        dto.setRequisitionCode(master.getRequisitionCode());
        dto.setOverallProcessStatus(master.getOverallProcessStatus());
        dto.setBusinessUnit(master.getBusinessUnit());
        dto.setCurrentLayer(master.getCurrentLayerPosition());
        dto.setPositionName(master.getPositionName());

        dto.setPersonalAreaCode(master.getPersonalAreaCode());
        dto.setPersonalAreaName(master.getPersonalAreaName());
        dto.setWorkplaceCode(master.getWorkplaceCode());
        dto.setWorkplaceName(master.getWorkplaceName());
        dto.setEmployeeCategoryCode(master.getEmployeeCategoryCode());
        dto.setEmployeeCategoryName(master.getEmployeeCategoryName());
        dto.setCompanyCode(master.getCompanyCode());
        dto.setCompanyName(master.getCompanyName());
        dto.setEmployeeSubGroupCode(master.getEmployeeSubGroupCode());
        dto.setEmployeeSubGroupName(master.getEmployeeSubGroupName());
        dto.setCurrentRole(master.getLastActionType() != null ? master.getLastActionType().name() : null);
        dto.setPositionCode(master.getPositionCode());
        dto.setProcessInitiationDate(master.getProcessInitiationDate());
        dto.setCurrentStatus(master.getCurrentStatus().name());
        dto.setRequisitionType(master.getRequisitionType());
        dto.setReportingTo(master.getReportingTo());

        // --- 2. Raiser Info ---
        if (raiserChannel != null) {
            dto.setRaiserUserId(raiserChannel.getMemberCode());
            dto.setRaiserUsername(raiserChannel.getPanelMember());
        } else {
            dto.setRaiserUserId(master.getUserId());
            dto.setRaiserUsername(master.getUserName());
        }

        // --- 3. Initial Proposed Headcount & Allocations ---
        if (master.getAllocations() != null && !master.getAllocations().isEmpty()) {
            master.getAllocations().stream().findFirst()
                    .ifPresent(present -> dto.setAnyPlanApproved(present.isAnyPlanApproved()));

            int totalProposedHeadcount = master.getAllocations().stream()
                    .filter(f -> f.getRecordStatus() == RecordStatus.ACTIVE)
                    .mapToInt(alloc -> alloc.getNoOfRequirements() != null ? alloc.getNoOfRequirements() : 0)
                    .sum();
            dto.setInitialProposedHeadcount(totalProposedHeadcount);

            dto.setRequisitionAllocations(master.getAllocations().stream()
                    .map(EntityAndDtoAllMapper::allocationEntityToDto).collect(Collectors.toList()));
        } else {
            dto.setRequisitionAllocations(Collections.emptyList());
        }
        //  PROGRESS CALCULATIONS (Core Logic) ---
        // রাইসার বাদে শুধুমাত্র অ্যাপ্রুভাল লেয়ারগুলো নিন (X of 4 এর '4')
        List<Integer> approvalLayers = allChannels.stream()
                .map(RequisitionApprovedChannel::getLayerPosition)
                .filter(l -> l != null && l > 0)
                .distinct()
                .sorted()
                .toList();

        dto.setTotalApprovalStages(approvalLayers.size());
        // হিস্ট্রি এবং পেন্ডিং ডাটা নিয়ে আসা
        List<RequisitionGroupAction> allGroupActions = requisitionGroupActionRepository
                .findAllByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeAsc(mId);

        List<RequisitionPendingAction> pendingActions = requisitionPendingActionRepository
                .findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(mId);
       // বর্তমানে ফাইলটি যে লেয়ারে পেন্ডিং আছে
        Integer currentPendingLayer = pendingActions.stream()
                .map(RequisitionPendingAction::getLayerPosition)
                .min(Integer::compare)
                .orElse(null);

      // *** এই অংশটুকু পরিবর্তন করুন (UNIQUE ACTED LAYERS) ***
      // আমরা শুধু সেই লেয়ারগুলো নেব যারা "ফরওয়ার্ড" বা "অ্যাপ্রুভ" করেছে, "ব্যাক" করেছে এমন লেয়ার নয়।
        Set<Integer> uniqueActedLayers = allGroupActions.stream()
                .filter(ga -> ga.getLayerPosition() != null && ga.getLayerPosition() > 0)
                // যারা ব্যাক করেছে তাদের বাদ দিন
                .filter(ga -> ga.getActionType() != RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION
                        && ga.getActionType() != RequisitionActionType.BACK
                        && ga.getActionType() != RequisitionActionType.HOLD_OFF)
                .map(RequisitionGroupAction::getLayerPosition)
                .collect(Collectors.toSet());

      // স্কিপড লেয়ারগুলো সংগ্রহ
        Set<Integer> skippedLayers = allChannels.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsSkiped()) && c.getLayerPosition() != null && c.getLayerPosition() > 0)
                .map(RequisitionApprovedChannel::getLayerPosition)
                .collect(Collectors.toSet());

        int completedStagesCount = 0;

        if (master.getOverallProcessStatus() == OverallProcessStatus.COMPLETED) {
            completedStagesCount = approvalLayers.size();
        } else {
            // লজিক: সম্পন্ন লেয়ার = (যারা সফলভাবে পাস করেছে + যারা স্কিপড হয়েছে)
            Set<Integer> allFinishedLayers = new HashSet<>(uniqueActedLayers);
            allFinishedLayers.addAll(skippedLayers);
            // ফাইল এখন যার ডেস্কে পেন্ডিং, সেই লেয়ারটি 'Completed' থেকে বাদ যাবে
            if (currentPendingLayer != null) {
                allFinishedLayers.remove(currentPendingLayer);
            }
            completedStagesCount = allFinishedLayers.size();
        }
        dto.setTotalCompletedStages(Math.min(completedStagesCount, approvalLayers.size()));
        dto.setTotalStageSkippedCount(skippedLayers.size());
        //3. Current Proposed Status & User Recommendations (Group Action Table) ---
        // BUSINESS LOGIC: Get the latest recommended headcount as "Proposal Current Status"
        allGroupActions.stream()
                .reduce((first, second) -> second) //
                .ifPresentOrElse(lastGa -> {
                    // Set the name of the user who took the latest action
                    dto.setLastActedUserName(lastGa.getActedUserName());
                    dto.setLastActedUserId(lastGa.getActedUserCode());
                  //  dto.setLastMemberOpinion(lastGa.getActionType().name());
                    dto.setLastMemberOpinion(ActionFormatterHelper.getReportActionName(lastGa.getActionType().name()));
                    dto.setLastMovementDate(lastGa.getCreatedDateTime());
                    if (lastGa.getRecommendedAllocations() != null) {
                        dto.setRecommendedAllocations(lastGa.getRecommendedAllocations());
                    } else dto.setRequisitionAllocations(Collections.emptyList());
                    // Set the latest recommended headcount if it is provided
                    if (lastGa.getRecommendedHeadcount() != null) {
                        dto.setCurrentProposedHeadcount(lastGa.getRecommendedHeadcount());
                    }
                }, () -> {
                    // Default to the original raiser if no actions have been recorded yet
                    dto.setLastActedUserName(master.getUserName());
                });

        dto.setUserWiseRecommendations(allGroupActions.stream().map(action -> {

            UserRecommendedHeadcountDto recDto = new UserRecommendedHeadcountDto();
            recDto.setActedUserId(action.getActedUserCode());
            recDto.setActedUserName(action.getActedUserName());
            recDto.setHeadcount(action.getRecommendedHeadcount());
            return recDto;
        }).collect(Collectors.toList()));

        //4. Pending Action & Current Desk tracking ---
        List<RequisitionPendingAction> currentPendings = requisitionPendingActionRepository
                .findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(mId);

        String deskInfo = "";

        if (master.getCurrentStatus() == RequisitionStatus.DRAFT) {
            deskInfo = (raiserChannel != null) ? raiserChannel.getPanelMember() : master.getUserName();
        } else {
            // ১. প্রসেসিং থাকলে বর্তমান পেন্ডিং ইউজারদের দেখাবে
            deskInfo = currentPendings.stream()
                    .map(RequisitionPendingAction::getPendingUserName)
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .collect(Collectors.joining(", "));

            // ২. যদি পেন্ডিং না থাকে (প্রসেস ক্লোজড), তবে শেষ কে অ্যাকশন নিয়েছে তার নাম দেখাবে
            if (deskInfo.isEmpty()) {
                // হিস্ট্রি থেকে সর্বশেষ অ্যাকশনটি খুঁজে বের করা
                String lastActorName = allGroupActions.stream()
                        .reduce((first, second) -> second) // সর্বশেষ রেকর্ড
                        .map(RequisitionGroupAction::getActedUserName)
                        .orElse("N/A");

                if (master.getCurrentStatus() == RequisitionStatus.NOT_APPROVED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getCurrentStatus() == RequisitionStatus.FINAL_APPROVED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getOverallProcessStatus() == OverallProcessStatus.NOT_RECOMMENDED) {
                    deskInfo = lastActorName;
                    deskInfo = "ARCHIVED";
                } else if (master.getOverallProcessStatus() == OverallProcessStatus.WITHDRAWN) {
                    deskInfo = "WITHDRAWN";
                    deskInfo = "ARCHIVED";
                } else {
                    deskInfo = "CLOSED";
                    deskInfo = "ARCHIVED";
                }
            }
        }
        dto.setCurrentDesk(deskInfo);

        dto.setCurrentPendingUsers(currentPendings.stream()
                .map(p -> p.getPendingUserId().toString())
                .collect(Collectors.joining(", ")));

        dto.setMandatoryActionRemaining(currentPendings.stream().anyMatch(RequisitionPendingAction::getIsMandatory));

        // 5. Last Movement & Opinion Tracking (Requisition Action Table) ---
        requisitionActionRepository.findTopByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeDesc(mId)
                .ifPresentOrElse(lastAction -> {
                    dto.setLastTransitionType(lastAction.getActionType().name());
                    dto.setLastTransitionRole(lastAction.getActedRole());
                    dto.setLastActionRemarks(lastAction.getRemarks());
                    // NEW FIELDS MAPPING:
                    dto.setApprovalStageCompleted(lastAction.getActedRole()); // Completed role/stage
                }, () -> {
                    dto.setLastTransitionType("INITIAL");
                    dto.setLastTransitionRole("RAISER");
                   // dto.setLastMovementDate(master.getCreatedDateTime());
                    dto.setLastActionRemarks("");
                    dto.setApprovalStageCompleted("RAISER");
                });

          // 6. Desktop Logic (Determines visibility and actions in "My Desk") ---
         // Identify if the logged-in user is the original creator
        boolean isCreator = master.getUserId().equalsIgnoreCase(loggedInUserId);
        // Identify if the logged-in user is defined as the Layer 0 Raiser in the approval channel
        Optional<RequisitionApprovedChannel> layer0Channel = allChannels.stream()
                .filter(c -> c.getLayerPosition() != null && c.getLayerPosition() == 0
                        && c.getMemberCode().equalsIgnoreCase(loggedInUserId))
                .findFirst();

        boolean isLayer0Raiser = layer0Channel.isPresent();
        // Check if the user has a record in the Pending Action table
        boolean isPendingForMe = currentPendings.stream()
                .anyMatch(p -> p.getPendingUserId().equalsIgnoreCase(loggedInUserId) &&
                        p.getLayerPosition().equals(master.getCurrentLayerPosition()));

        // CASE 1: Requisition is in DRAFT status ---
        if (master.getCurrentStatus() == RequisitionStatus.DRAFT) {

            if (isLayer0Raiser) {
                dto.setActionRequiredByMe(true);
                // ড্রাফট অবস্থায় শুধু প্রথমবার পাঠানোর অপশন থাকবে
                dto.setAvailableActions(List.of("FORWARD_FOR_RECOMMENDATION"));
            } else if (isCreator) {
                dto.setActionRequiredByMe(true);
                dto.setAvailableActions(Collections.emptyList());
            } else {
                dto.setActionRequiredByMe(false);
            }

        }
        // CASE 2: Requisition is being PROCESSED, BACKED, UNDER_REVIEW or POSTPONED ---
        else if ((master.getOverallProcessStatus() == OverallProcessStatus.PROCESSING
                || master.getCurrentStatus() == RequisitionStatus.BACKED
                || master.getCurrentStatus() == RequisitionStatus.UNDER_REVIEW
                || master.getCurrentStatus() == RequisitionStatus.POSTPONED) && isPendingForMe) {

            dto.setActionRequiredByMe(true);

            Optional<RequisitionApprovedChannel> userChannel = allChannels.stream()
                    .filter(c -> c.getMemberCode().equalsIgnoreCase(loggedInUserId)
                            && c.getLayerPosition().equals(master.getCurrentLayerPosition()))
                    .findFirst();

            AuthorizationType authType = userChannel.map(RequisitionApprovedChannel::getAuthorizationType)
                    .orElse(AuthorizationType.APPROVER);

            List<String> actions = new ArrayList<>();

            // ১. ইতিহাসে সবচেয়ে লেটেস্ট 'BACK' অ্যাকশনটি বের করুন যা আমার বর্তমান লেয়ারের উপরের কোনো লেয়ার থেকে এসেছিল
            // এটিই নিশ্চিত করবে যে মাঝখানের ইউজার (Layer 3) নিজে ব্যাক করলেও উপরের (Layer 4) ব্যাক-ফ্লোর গুরুত্ব বেশি পাবে।
            Optional<RequisitionGroupAction> latestUpperLayerBack = allGroupActions.stream()
                    .filter(ga -> (ga.getActionType() == RequisitionActionType.BACK
                            || ga.getActionType() == RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION)
                            && ga.getLayerPosition() > master.getCurrentLayerPosition())
                    .reduce((first, second) -> second);

            boolean isStillInCorrectionFlow = false;

            if (latestUpperLayerBack.isPresent()) {
                Integer targetBackOriginLayer = latestUpperLayerBack.get().getLayerPosition();

                // ২. চেক করুন: ওই উপরের লেয়ারের ব্যাক করার পর থেকে এখন পর্যন্ত কি ফাইলটি সেখানে বা তার উপরে পৌঁছেছে?
                boolean hasReachedTarget = allGroupActions.stream()
                        .filter(ga -> ga.getId() > latestUpperLayerBack.get().getId())
                        .anyMatch(ga -> ga.getLayerPosition() >= targetBackOriginLayer);

                if (!hasReachedTarget) {
                    isStillInCorrectionFlow = true;
                }
            }
            // ৩. বাটন ডিসিশন লজিক (ইন্টেলিজেন্ট সুইচ) ---
            if (isStillInCorrectionFlow) {
                // আমি যদি মাঝখানের লেয়ার হই এবং আমার উপরে কেউ ব্যাক করে থাকে যা এখনও ক্লিয়ার হয়নি
                actions.add("FORWARD_AGAIN");

                if (master.getCurrentLayerPosition() == 0 || authType == AuthorizationType.INITIATOR) {
                    actions.add("WITHDRAW");
                }
            } else {
                // নরমাল ফ্লো অথবা আমিই সেই লেয়ার যেখান থেকে ব্যাক শুরু হয়েছিল
                switch (authType) {
                    case INITIATOR -> {
                        actions.add("WITHDRAW");
                        actions.add("FORWARD_FOR_RECOMMENDATION");
                    }
                    case RECOMMENDER -> {
                        actions.add("FORWARD_FOR_REVIEW"); // FORWARD_FOR_FURTHER_REVIEW
                        actions.add("RECOMMEND");
                        actions.add("NOT_RECOMMEND");
                    }
                    case COORDINATOR -> {
                        actions.add("FORWARD_FOR_RECOMMENDATION");
                        actions.add("FORWARD_FOR_REVIEW"); // FORWARD_FOR_FURTHER_REVIEW
                    }
                    case APPROVER -> {
                        actions.add("APPROVE");
                        actions.add("NOT_APPROVE");
                    }
                    case ANALYST -> {
                        actions.add("ANALYSIS");
                        actions.add("NOT_ANALYSIS");
                    }
                    case VERIFIER -> {
                        actions.add("VERIFY");
                        actions.add("NOT_VERIFY");
                    }
                    default -> actions.add("FORWARD_FOR_REVIEW");
                }
            }
            // --- ৪. ব্যাক করার পারমিশন (যাতে চেইন ব্রেক না হয়) ---
            userChannel.ifPresent(channel -> {
                boolean matrixCanBack = (authType == AuthorizationType.RECOMMENDER ||
                        authType == AuthorizationType.COORDINATOR ||
                        authType == AuthorizationType.APPROVER);

                if (matrixCanBack && channel.checkPermission("BACK")) {
                    actions.add("SEND_BACK_FOR_FURTHER_INFO_ACTION");
                }

                if (master.getCurrentStatus() != RequisitionStatus.POSTPONED) {
                    if (channel.checkPermission("HOLD_OFF") || channel.checkPermission("POSTPONE")) {
                        actions.add("HOLD_OFF");
                    }
                }
            });

            dto.setAvailableActions(actions.stream().distinct().collect(Collectors.toList()));
        }
       // --- CASE 3: No active involvement ---
        else {
            dto.setActionRequiredByMe(false);
            dto.setAvailableActions(Collections.emptyList());
        }
        return dto;
    }


    @Override
    public BaseResponse getAutoNextStepChannelDetails(Long masterId) {
        BaseResponse baseResponse = new BaseResponse();

        RecruitmentRequisitionMaster master = recruitmentRequisitionMasterRepository.
                findByIdAndRecordStatus(masterId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Master Requisition not found"));

        // CRITICAL: Get the ID of the user currently looking at the screen.
        // Replace this with your actual Security Context utility.
        String currentUserId = master.getNextRole();

        Integer currentLayer = (master.getCurrentLayerPosition() == null) ? 0 : master.getCurrentLayerPosition();
        List<RequisitionApprovedChannel> finalDisplayList = new ArrayList<>();

        // 1. Check for Multi-Back Jump History for THIS specific user
        Integer previousSenderLayer = findSpecificSenderLayer(masterId, currentLayer, currentUserId);

        // 2. JUMP CASE: If the file came BACK to this user from a higher layer
        if (previousSenderLayer != null && previousSenderLayer > currentLayer) {

            Optional<RequisitionAction> lastAction = requisitionActionRepository
                    .findTopByRecruitmentRequisitionMasterIdAndToLayerPositionAndToStageAndActionTypeInOrderByIdDesc(
                            masterId,
                            currentLayer,
                            currentUserId,
                            List.of(RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION));

            if (lastAction.isPresent()) {
                String originalSenderCode = lastAction.get().getActedBy();

                // Show only the specific user who sent it back (Auto-selection)
                finalDisplayList = requisitionApprovedChannelRepository
                        .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, previousSenderLayer)
                        .stream()
                        .filter(c -> c.getMemberCode().equals(originalSenderCode))
                        .collect(Collectors.toList());
            }

            // Safety fallback: if specific user filter fails, show the whole layer
            if (finalDisplayList.isEmpty()) {
                finalDisplayList = requisitionApprovedChannelRepository
                        .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, previousSenderLayer);
            }
        }
        // 3. RAISER CASE: Starting from Draft
        else if (currentLayer == 0) {
            finalDisplayList = findNextAvailableLayer(masterId, 1, master.getBusinessUnit());
        }
        // 4. NORMAL FLOW CASE (Including Parallel Logic)
        else {
            List<RequisitionApprovedChannel> currentConfigChannels = requisitionApprovedChannelRepository.
                    findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, currentLayer)
                    .stream().filter(f-> f.getRecordStatus()== RecordStatus.ACTIVE).toList();
            List<RequisitionPendingAction> assignedActions = requisitionPendingActionRepository.
                    findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, currentLayer);

            List<String> alreadyActedUserIds = assignedActions.stream()
                    .filter(RequisitionPendingAction::isHasActed)
                    .map(RequisitionPendingAction::getPendingUserId)
                    .collect(Collectors.toList());

            // FIX: Check if anyone ELSE (other than the current user) is still mandatory in this layer
            List<RequisitionApprovedChannel> remainingMandatoryOthers = currentConfigChannels.stream()
                    .filter(c -> !alreadyActedUserIds.contains(c.getMemberCode()) // Not acted yet
                            && !c.getMemberCode().equals(currentUserId)          // Is NOT the current logged-in user
                            && Boolean.TRUE.equals(c.getIsMandatoryAction()))    // Is mandatory
                    .collect(Collectors.toList());

            if (!remainingMandatoryOthers.isEmpty()) {
                // Stay in current layer because others must act
                finalDisplayList = remainingMandatoryOthers;
            } else {
                // Move to Layer + 1 (Skip logic applied)
                finalDisplayList = findNextAvailableLayer(masterId, currentLayer + 1, master.getBusinessUnit());
            }
        }

        // --- 5. MAPPING LOGIC (Wrapping for UI) ---
        if (finalDisplayList != null && !finalDisplayList.isEmpty()) {
            Map<Integer, List<RequisitionApprovedChannel>> grouped = finalDisplayList.stream()
                    .collect(Collectors.groupingBy(RequisitionApprovedChannel::getLayerPosition,
                            LinkedHashMap::new, Collectors.toList()));

            List<RequisitionLayerWrapperDto> wrapperList = grouped.entrySet().stream().map(entry -> {
                RequisitionApprovedChannel firstMember = entry.getValue().get(0);
                RequisitionLayerWrapperDto layerDto = new RequisitionLayerWrapperDto();
                layerDto.setLayerPosition(entry.getKey());
                layerDto.setProcessTitle(firstMember.getProcessTitle());
                layerDto.setIsStageMandatory(firstMember.getIsStageMandatory());
                layerDto.setIsSkippable(firstMember.isSkippable());

                List<RequisitionMemberDto> memberDtos = entry.getValue().stream().map(m -> {
                    RequisitionMemberDto mem = new RequisitionMemberDto();
                    mem.setMemberCode(m.getMemberCode());
                    mem.setPanelMember(m.getPanelMember());
                    mem.setApprovedType(m.getApprovedType());
                    mem.setIsMandatoryAction(m.getIsMandatoryAction());
                    mem.setPermissionLevels(m.getPermissionLevels());
                    return mem;
                }).collect(Collectors.toList());

                layerDto.setMembers(memberDtos);
                return layerDto;
            }).collect(Collectors.toList());

            baseResponse.setRequisitionLayerWrapperDtos(wrapperList);
        }

        baseResponse.setMessage(ResponseEnum.SUCCESS.getStatus());
        return baseResponse;
    }

    /**
     * Helper Method to find the next valid layer by considering isSkiped and Finance logic
     */
    private List<RequisitionApprovedChannel> findNextAvailableLayer(Long masterId, Integer targetLayer, String businessUnit) {

        List<RequisitionApprovedChannel> channels = requisitionApprovedChannelRepository.
                findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, targetLayer)
                .stream().filter(f-> f.getRecordStatus()== RecordStatus.ACTIVE ).toList();

        if (channels.isEmpty()) return channels; // End of flow

        // 1. Check if this layer is marked as Skiped in the DB
        if (Boolean.TRUE.equals(channels.get(0).getIsSkiped())) {
            return findNextAvailableLayer(masterId, targetLayer + 1, businessUnit);
        }

        // 2. Check for Finance HOD Skip (URS Logic)
        boolean shouldSkipFinance = channels.stream().anyMatch(channel ->
                channel.getPanelMember() != null &&
                        channel.getPanelMember().contains("FINANCE_HOD") &&
                        !"SALES_AND_DISTRIBUTION".equalsIgnoreCase(businessUnit)
        );

        if (shouldSkipFinance) {
            return findNextAvailableLayer(masterId, targetLayer + 1, businessUnit);
        }

        return channels;
    }


    private Integer findSpecificSenderLayer(Long masterId, Integer currentLayer, String currentUserId) {
        return requisitionActionRepository.findTopByRecruitmentRequisitionMasterIdAndToLayerPositionAndToStageAndActionTypeInOrderByIdDesc(
      //  return requisitionActionRepository.findTopByManpowerPlanningMasterIdAndToLayerPositionAndToStageAndActionTypeInOrderByIdDesc(
                masterId,
                currentLayer,
                currentUserId, // বর্তমান ইউজারকে যে ব্যাক পাঠিয়েছিল তাকেই কেবল খোঁজা হবে
                // was BACK
                List.of(RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION)
        ).map(RequisitionAction::getFromLayerPosition).orElse(null);
    }

    private String findSpecificSenderUser(Long masterId, Integer currentLayer, String currentUserId) {
        return requisitionActionRepository.findTopByRecruitmentRequisitionMasterIdAndToLayerPositionAndToStageAndActionTypeInOrderByIdDesc(
                masterId,
                currentLayer,
                currentUserId,
                // was BACK
                List.of(RequisitionActionType.SEND_BACK_FOR_FURTHER_INFO_ACTION)
        ).map(RequisitionAction::getActedBy).orElse(null); // যে ব্যাক করেছিল তার ID
    }


    // skip list if that user don't take any action in the same layer
    @Override
    public BaseResponse getPreviousLayerChannels(Long masterId) {
        BaseResponse baseResponse = new BaseResponse();

        // 1. Fetch Master Requisition
        RecruitmentRequisitionMaster master = recruitmentRequisitionMasterRepository
                .findByIdAndRecordStatus(masterId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("RecruitmentRequisitionMaster not found"));

        Integer currentLayer = (master.getCurrentLayerPosition() == null) ? 0 : master.getCurrentLayerPosition();

        // 2. Fetch all actual actions taken to identify who participated in each layer
        List<RequisitionGroupAction> actualActions = requisitionGroupActionRepository
                .findAllByRecruitmentRequisitionMasterId(masterId);

        // 3. Fetch and Filter Channels
        List<RequisitionApprovedChannel> previousChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterId(masterId)
                .stream()
                .filter(f-> f.getRecordStatus()== RecordStatus.ACTIVE)
                // Filter: Must be a previous layer and not draft
                .filter(c -> c.getLayerPosition() < currentLayer)
                // Business Rule: Exclude layers that were automatically skipped
                .filter(c -> !Boolean.TRUE.equals(c.getIsSkiped()))

                //  Only include the specific user if they exist in the Group Action table for this master.
                //  This ensures that if 3 people were in a layer but only 1 acted, only that 1 is returned.

                .filter(c -> actualActions.stream()
                        .anyMatch(action -> action.getActedUserCode() != null &&
                                action.getActedUserCode().trim().equalsIgnoreCase(c.getMemberCode().trim())))
                .sorted(Comparator.comparing(RequisitionApprovedChannel::getLayerPosition).reversed())
                .toList();

        // 4. Mapping to Wrapper DTO
        if (!previousChannels.isEmpty()) {
            Map<Integer, List<RequisitionApprovedChannel>> grouped = previousChannels.stream()
                    .collect(Collectors.groupingBy(RequisitionApprovedChannel::getLayerPosition,
                            LinkedHashMap::new, Collectors.toList()));

            List<RequisitionLayerWrapperDto> wrapperList = grouped.entrySet().stream().map(entry -> {
                RequisitionApprovedChannel firstMember = entry.getValue().get(0);

                RequisitionLayerWrapperDto layerDto = new RequisitionLayerWrapperDto();
                layerDto.setLayerPosition(entry.getKey());
                layerDto.setProcessTitle(firstMember.getProcessTitle());
                layerDto.setIsStageMandatory(firstMember.getIsStageMandatory());
                layerDto.setIsSkippable(firstMember.isSkippable());

                List<RequisitionMemberDto> memberDtos = entry.getValue().stream().map(m -> {
                    RequisitionMemberDto mem = new RequisitionMemberDto();
                    mem.setMemberCode(m.getMemberCode());
                    mem.setPanelMember(m.getPanelMember());
                    mem.setApprovedType(m.getApprovedType());
                    mem.setIsMandatoryAction(m.getIsMandatoryAction());
                    mem.setPermissionLevels(m.getPermissionLevels());
                    return mem;
                }).collect(Collectors.toList());

                layerDto.setMembers(memberDtos);
                return layerDto;
            }).collect(Collectors.toList());

            baseResponse.setRequisitionLayerWrapperDtos(wrapperList);
        }

        baseResponse.setMessage(ResponseEnum.SUCCESS.getStatus());
        return baseResponse;
    }

    @Override
    @Transactional
    public BaseResponse skipStage(Long masterId, Integer layerToSkip) {
        BaseResponse response = new BaseResponse();

        // 1. Fetch the Master and Current User (for logging)
        RecruitmentRequisitionMaster master = recruitmentRequisitionMasterRepository.findByIdAndRecordStatus(masterId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Master Requisition not found"));

        // 2. Fetch all members of the stage to be skipped
        List<RequisitionApprovedChannel> channels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterIdAndLayerPosition(masterId, layerToSkip)
                .stream().filter(f-> f.getRecordStatus() == RecordStatus.ACTIVE).toList();

        if (channels.isEmpty()) {
            throw new CustomException("Not found  RequisitionApprovedChannel for Layer Skip  " + layerToSkip);
        }
        // 3. Validation: Check if it's actually skippable
        if (Boolean.TRUE.equals(channels.get(0).getIsStageMandatory())) {
            throw new CustomException("This stage is mandatory and cannot be skipped.");
        }
        // 4. Update the skip flag in Channel table
        channels.forEach(channel ->
                channel.setIsSkiped(true)
        );
        requisitionApprovedChannelRepository.saveAll(channels);

        response.setMessage("Stage skipped successfully. The process will now move to the next available layer.");
        return response;
    }

    @Override
    public BaseResponse getQuickViewReport(Long masterId) {
        BaseResponse baseResponse = new BaseResponse();

        RecruitmentRequisitionMaster master = recruitmentRequisitionMasterRepository.findByIdAndRecordStatus(masterId, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Requisition Master Object Not Found By masterId"));

        List<RequisitionApprovedChannel> allChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterIdOrderByLayerPositionAsc(masterId).stream()
                .filter(f -> Boolean.FALSE.equals(f.getIsSkiped())
                        && f.getRecordStatus()==RecordStatus.ACTIVE).toList();

        Map<Integer, String> layerWiseMembers = allChannels.stream()
                .collect(Collectors.groupingBy(
                        RequisitionApprovedChannel::getLayerPosition,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < list.size(); i++) {
                                RequisitionApprovedChannel c = list.get(i);
                                sb.append(c.getPanelMember()).append(", ")
                                        .append(c.getDesignation() != null ? c.getDesignation() : "");
                                if (i < list.size() - 1) sb.append("\n");
                            }
                            return sb.toString();
                        })
                ));

        List<RequisitionGroupAction> allActions = requisitionGroupActionRepository
                .findAllByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeAsc(masterId);

        List<RequisitionPendingAction> pendingActions = requisitionPendingActionRepository
                .findAllByRecruitmentRequisitionMasterIdAndHasActedFalse(masterId);

        String currentPendingUsers = pendingActions.stream()
                .map(p -> {
                    String designation = allChannels.stream()
                            .filter(c -> c.getMemberCode().equals(p.getPendingUserId()))
                            .map(RequisitionApprovedChannel::getDesignation)
                            .findFirst()
                            .orElse(null);
                    return p.getPendingUserName() + (designation != null && !designation.isEmpty() ? " (" + designation + ")" : "");
                })
                .distinct()
                .collect(Collectors.joining(", "));

        List<Map<String, Object>> reportData = new ArrayList<>();
        List<Integer> distinctLayers = allChannels.stream()
                .map(RequisitionApprovedChannel::getLayerPosition)
                .distinct().sorted().toList();

        for (int i = 0; i < distinctLayers.size(); i++) {
            Integer layerPos = distinctLayers.get(i);
            String joinedMembers = layerWiseMembers.get(layerPos);
            RequisitionApprovedChannel channelSample = allChannels.stream()
                    .filter(c -> c.getLayerPosition().equals(layerPos))
                    .findFirst().orElse(null);

            String processPanelCode = (channelSample != null) ? channelSample.getProcessPanelCode() : "N/A";

            // ১. এই লেয়ারের সর্বশেষ অ্যাকশন খুঁজে বের করা
            RequisitionGroupAction latestAction = allActions.stream()
                    .filter(a -> a.getLayerPosition().equals(layerPos))
                    .reduce((first, second) -> second)
                    .orElse(null);

            // ২. এই লেয়ারটি বর্তমানে পেন্ডিং কি না চেক করা
            boolean isCurrentlyAtThisLayer = pendingActions.stream()
                    .anyMatch(p -> p.getLayerPosition().equals(layerPos));

            // ৩. Held Off এর জন্য বিশেষ লজিক ---
            // যদি মাস্টার POSTPONED হয় এবং এই লেয়ারে সর্বশেষ অ্যাকশনটি HOLD_OFF হয়
            boolean isHeldOffInThisLayer = latestAction != null
                    && master.getCurrentStatus() == RequisitionStatus.POSTPONED
                    && "HOLD_OFF".equalsIgnoreCase(latestAction.getActionType().name());

            if (isHeldOffInThisLayer) {
                // যদি Held Off হয়ে থাকে, তবে আমরা mapToReportRow কল করবো যাতে ইউজার, ডেট ও কমেন্ট দেখা যায়
                String customOpinion = "Held Off(Current Desk)";

                String actedUserDesignation = allChannels.stream()
                        .filter(c -> c.getMemberCode().equals(latestAction.getActedUserCode()))
                        .map(RequisitionApprovedChannel::getDesignation)
                        .findFirst().orElse("");

                reportData.add(mapToReportRow(channelSample, latestAction, joinedMembers, currentPendingUsers, customOpinion, actedUserDesignation, processPanelCode));

            } else if (isCurrentlyAtThisLayer) {
                // বর্তমানে ফাইলটি এই ডেস্কে আছে (নরমাল পেন্ডিং অবস্থা)
                reportData.add(mapToPendingRow(channelSample, joinedMembers, currentPendingUsers, "Current Desk", processPanelCode));

            } else if (latestAction != null) {
                // সাধারণ অ্যাকশন সম্পন্ন হওয়া লেয়ার
                String currentDeskInfo = !pendingActions.isEmpty() ? currentPendingUsers :
                        (i == distinctLayers.size() - 1 ? "Process Completed" : "");

                String opinionName = ActionFormatterHelper.getReportActionName(latestAction.getActionType().name());

                String actedUserDesignation = allChannels.stream()
                        .filter(c -> c.getMemberCode().equals(latestAction.getActedUserCode()))
                        .map(RequisitionApprovedChannel::getDesignation)
                        .findFirst().orElse("");

                reportData.add(mapToReportRow(channelSample, latestAction, joinedMembers, currentDeskInfo, opinionName, actedUserDesignation, processPanelCode));

            } else {
                // লেয়ারটি এখনও পৌঁছায়নি
                reportData.add(mapToPendingRow(channelSample, joinedMembers, "", " ", processPanelCode));
            }
        }

        baseResponse.setMapBasedReportList(reportData);
        baseResponse.setMessage("Success");
        return baseResponse;
    }

    private Map<String, Object> mapToReportRow(RequisitionApprovedChannel channel
            , RequisitionGroupAction groupAction, String availableMembers,
                                               String currentDeskInfo, String opinion, String designation,
                                                 String  processPanelCode) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("position", channel.getLayerPosition());
        row.put("roleTitle", channel.getAuthorizationType().name());
        row.put("availablePanelMembers", availableMembers);

        // actualPanelMembers এর সাথে ডেজিগনেশন যোগ করা হলো (ইচ্ছানুযায়ী ফরম্যাট: নাম (ডেজিগনেশন))
        String actualMemberWithDesignation = groupAction.getActedUserName() +
                (designation != null && !designation.isEmpty() ? " , "+ designation  : "");
        row.put("actualPanelMembers", actualMemberWithDesignation);
        row.put("processPanelCode", processPanelCode); // নতুন ফিল্ড


        row.put("noOfRequirements", groupAction.getRecommendedHeadcount());
        row.put("currentDeskInfo", currentDeskInfo);
         row.put("opinion", ActionFormatterHelper.getReportActionName(opinion));
        row.put("comment", groupAction.getRemarks() != null ? groupAction.getRemarks() : "");
        row.put("date", groupAction.getCreatedDateTime());
        row.put("status", "COMPLETED");
        row.put("processPanelCode", processPanelCode); // নতুন ফিল্ড
        return row;
    }

    private Map<String, Object> mapToPendingRow(RequisitionApprovedChannel channel, String availableMembers,
                                                String currentDeskInfo, String opinion,String  processPanelCode) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("position", channel.getLayerPosition());
        row.put("roleTitle", channel.getAuthorizationType().name());
        row.put("availablePanelMembers", availableMembers);
        row.put("actualPanelMembers", "---");
        row.put("noOfRequirements", 0);
        row.put("currentDeskInfo", currentDeskInfo);

        row.put("opinion", ActionFormatterHelper.getReportActionName(opinion));
        row.put("comment", "");
        row.put("date", "---");
        row.put("status", "NOT_REACHED");
        row.put("processPanelCode", processPanelCode); // নতুন ফিল্ড
        return row;
    }

    @Override
    public BaseResponse getTotalActivityLogReport(Long masterId) {
        BaseResponse baseResponse = new BaseResponse();

        // ১. মাস্টার অবজেক্ট নিয়ে আসা
        RecruitmentRequisitionMaster master = recruitmentRequisitionMasterRepository.findById(masterId)
                .orElseThrow(() -> new CustomException("Master not found"));

        // ২. অ্যাকশন হিস্ট্রি নিয়ে আসা
        List<RequisitionGroupAction> actions = requisitionGroupActionRepository
                .findAllByRecruitmentRequisitionMasterIdOrderByCreatedDateTimeAsc(masterId);

        // ৩. চ্যানেল ডাটা নিয়ে আসা
        List<RequisitionApprovedChannel> allChannels = requisitionApprovedChannelRepository
                .findAllByRecruitmentRequisitionMasterId(masterId)
                .stream().filter(f-> f.getRecordStatus()== RecordStatus.ACTIVE).toList();

        List<Map<String, Object>> reportList = new ArrayList<>();
        int sl = 1;

        for (int i = 0; i < actions.size(); i++) {
            RequisitionGroupAction action = actions.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sl", sl++);

            // ডেজিগনেশন লজিক
            String designation = allChannels.stream()
                    .filter(c -> c.getMemberCode() != null && c.getMemberCode().equals(action.getActedUserCode()))
                    .map(RequisitionApprovedChannel::getDesignation)
                    .findFirst()
                    .orElse("");

            String nameWithDesignation = action.getActedUserName() +
                    (!designation.isEmpty() ? ", " + designation : "");

            row.put("panelMembers", nameWithDesignation);
            row.put("noOfRequirements", action.getRecommendedHeadcount());

            // --- ৪. সুনির্দিষ্ট Held Off লজিক ---
            String rawActionName = (action.getActionType() != null) ? action.getActionType().name() : "";
            String formattedOpinion = ActionFormatterHelper.getReportActionName(rawActionName);

            boolean isLastAction = (i == actions.size() - 1);
            // আপনার Helper অনুযায়ী key হলো "HOLD_OFF"
            if (isLastAction
                    && master.getCurrentStatus() == RequisitionStatus.POSTPONED
                    && "HOLD_OFF".equalsIgnoreCase(rawActionName)) {

                formattedOpinion = "Held Off(Current Desk)";
            }
            row.put("opinion", formattedOpinion);
            row.put("comment", action.getRemarks() != null ? action.getRemarks() : "");
            row.put("date", action.getCreatedDateTime());

            reportList.add(row);
        }

        baseResponse.setMapBasedReportList(reportList);
        baseResponse.setMessage(ResponseEnum.SUCCESS.getStatus());
        return baseResponse;
    }

    @Override
    public List<RequisitionListViewDto> findAllRequisitionByApprovedPlanId(String approvedPlanId) {

        List<RecruitmentRequisitionMaster> masterList = recruitmentRequisitionMasterRepository.
                findAllRequisitionByApprovedPlanId(approvedPlanId);

        List<RequisitionListViewDto> dtoList = masterList.stream()
                .map(this::mapMasterToListViewForApprovedPlanIdDto).toList();

        return dtoList;
    }

    private String getDynamicOpinion(RequisitionGroupAction currentAction, List<RequisitionGroupAction> allGroupActions) {
        String actionType = (currentAction.getActionType() != null) ? currentAction.getActionType().name() : "";
        String currentRole = (currentAction.getCurrentRole() != null) ? currentAction.getCurrentRole() : "";

        // 1. Find the immediate previous action from the history list to determine the flow
        RequisitionGroupAction previousAction = null;
        int currentIndex = allGroupActions.indexOf(currentAction);
        if (currentIndex > 0) {
            previousAction = allGroupActions.get(currentIndex - 1);
        }
        // 2. Check if the previous action was a 'BACK' to identify if the current user is responding to it
        boolean isResponseToBack = previousAction != null &&
                "BACK".equalsIgnoreCase(previousAction.getActionType().name());
        // 3. Dynamic Opinion Logic ---
        // A. Logic for the RAISER
        if (currentRole.equalsIgnoreCase("RAISER") && actionType.equalsIgnoreCase("SUBMIT")) {
            // If the immediate previous action was 'BACK', the raiser is resubmitting after a correction
            return isResponseToBack ? "Forwarded for further recommendation" : "Forwarded for recommendation";
        }
        // B. Logic for other Reviewers/Coordinators (e.g., Shaheenul)
        // If they received a 'BACK' and are now forwarding, it qualifies as a 'Further Review'
        if (isResponseToBack && !actionType.equalsIgnoreCase("BACK")) {
            return "Forwarded for further review";
        }
        // C. Logic for the user who initiated the BACK action (e.g., Pranesh)
        if (actionType.equalsIgnoreCase("BACK")) {
            return "BACK";
        }

        // Default: For all other cases (Normal flow or subsequent reviewers), return the original action name
        return actionType.replace("_", " ");
    }


    @Override
    public BaseResponse resetProcessInitiationDate(PIDResetParam request) {
         return null;
    }

    @Override
    public BaseResponse getRequisitionHistory(Long requisitionId) {
         return null;
     }

    @Override
    public BaseResponse getAllRequisitionActions(Long requisitionId) {
         return null;
     }

    @Override
    public BaseResponse getGroupActionDetailsByLayer(Long masterId, Integer layerPosition) {
         return null;
     }


}
