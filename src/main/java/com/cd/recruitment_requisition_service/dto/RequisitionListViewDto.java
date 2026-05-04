package com.cd.recruitment_requisition_service.dto;

import com.cd.recruitment_requisition_service.entity.RecommendedAllocations;
import com.cd.recruitment_requisition_service.enums.OverallProcessStatus;
import com.cd.recruitment_requisition_service.enums.RequisitionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionListViewDto {

    private Long id;
    private String requisitionCode;
    private String raiserUserId;
    private String raiserUsername;
    private String currentRole;
    private String lastActedUserId;
    private String lastActedUserName;

    private String currentStatus;

    private Integer totalApprovalStages;     // Total unique layers in the workflow
    private Integer totalCompletedStages;    // Count of layers where all mandatory actions are done
    private Integer totalStageSkippedCount;

    private String personalAreaCode;
    private String personalAreaName;
    private String workplaceCode;
    private String workplaceName;
    private String employeeCategoryCode;
    private String employeeCategoryName;
    private String companyCode;
    private String companyName;
    private String employeeSubGroupCode;
    private String employeeSubGroupName;

    private Boolean anyPlanApproved;
    private RequisitionType requisitionType;

    private String reportingTo;

    private String positionCode;
    private String positionName;

    private LocalDate processInitiationDate;

    private OverallProcessStatus overallProcessStatus;
    private Integer initialProposedHeadcount;
    private List<RequisitionRequirementsAllocationDto> requisitionAllocations=new ArrayList<>();
    private List<RecommendedAllocations> recommendedAllocations=new ArrayList<>();
    private String businessUnit;

    // --- New Fields Based on Your Business Requirement ---
    private Integer currentProposedHeadcount; // Proposal current status (Latest recommended headcount)
    private String lastMemberOpinion;         // Last panel member's opinion/remarks
    private String approvalStageCompleted;    // Which role/stage was completed last
    private String currentDesk;               // Current location of the file (Pending Roles)
    private String opinion;                   // Current user's actionType or last member's actionType

    // --- Pending Action Data (The "Where is it now?" tracking) ---
    private boolean isActionRequiredByMe = false;
    private String currentPendingUsers;
    private Integer currentLayer;
    private boolean isMandatoryActionRemaining;

    // --- Group Action Data (The "Who said what?" tracking) ---
    private List<UserRecommendedHeadcountDto> userWiseRecommendations;

    // --- Requisition Action Data (The "Last Movement" tracking) ---
    private String lastTransitionType;
    private String lastTransitionRole;
    private LocalDateTime lastMovementDate;
    private String lastActionRemarks;
    private List<String> availableActions;
}
