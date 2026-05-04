package com.cd.recruitment_requisition_service.entity;

import com.cd.recruitment_requisition_service.enums.AuthorizationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "requisition_approved_channel")
@Getter
@Setter
public class RequisitionApprovedChannel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rsdm_seq")
    @SequenceGenerator(name = "rsdm_seq", sequenceName = "requisition_supporting_documents_master_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_requisition_master_id", nullable = false)
    @JsonIgnore
    private RecruitmentRequisitionMaster recruitmentRequisitionMaster;

    @Column(name = "code")
    private String memberCode;

    @Column(name = "process_title", length = 255)
    private String processTitle;

    private String processPanelCode;

    @Column(name = "layer_position")
    private Integer layerPosition;

    @Column(name = "approved_type", length = 100)
    private String approvedType;

    @Column(name = "member", length = 255)
    private String panelMember;

    private String designation;

    @Enumerated(EnumType.STRING)
    private AuthorizationType authorizationType;

    //  NEW FIELD: Is this specific user's action mandatory?
    @Column(name = "is_mandatory_action", nullable = false)
    private Boolean isMandatoryAction = false;

    @Column(name = "is_stage_mandatory", nullable = false)
    private Boolean isStageMandatory = true; // Default True

    private Boolean isSkiped = false;

    /**
     * Stage skip kora jabe kina seta check korar helper
     *
     * @return boolean (True hole skip kora JABE)
     */
    public boolean isSkippable() {
        return Boolean.FALSE.equals(this.isStageMandatory);
    }

    //  UPDATED FIELD: Stores values like "BACK,POSTPONE"
    @Column(name = "permission_levels", length = 500)
    private String permissionLevels;

    /**
     * Helper to check if a specific permission exists in the list.
     * Logic: If the DB has 'HOLD_OFF', it treats it as 'POSTPONE'.
     *
     * @param action (e.g., "BACK" or "POSTPONE")
     */
    public boolean checkPermission(String action) {
        if (this.permissionLevels == null || this.permissionLevels.isEmpty()) {
            return false;
        }

        // Convert input action to uppercase for comparison
        String searchAction = action.trim().toUpperCase();

        return java.util.Arrays.stream(this.permissionLevels.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .anyMatch(p -> {
                    // Primary check: Direct match (e.g., "BACK" == "BACK")
                    if (p.equals(searchAction)) return true;

                    // if action vaue is SEND_BACK_FOR_FURTHER_INFO_ACTION but permisionlevel value is BACK
                    if (searchAction.equalsIgnoreCase("SEND_BACK_FOR_FURTHER_INFO_ACTION") && p.equalsIgnoreCase("BACK")) return  true;

                    // Business Logic: Map DB value "HOLD_OFF" to application value "POSTPONE"
                    if (searchAction.equals("POSTPONE") && p.equals("HOLD_OFF")) return true;

                    return false;
                });
    }

}
