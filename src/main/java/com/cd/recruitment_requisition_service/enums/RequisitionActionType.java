package com.cd.recruitment_requisition_service.enums;

public enum RequisitionActionType {
    // --- Core & Initial Actions ---
    SUBMIT,                           // Initial submission
    WITHDRAW,                         // Actor: Initiator (Matrix row 1)
    HOLD_OFF,                         // Actor: All (Matrix row 5)

    // --- Forwarding & Routing Logic (Matrix matches) ---
    FORWARD_FOR_RECOMMENDATION,       // Actor: Initiator, Coordinator (Matrix row 2)
    FORWARD_FOR_FURTHER_REVIEW,       // Actor: Recommender, Coordinator (Matrix row 3)
    FORWARD_AGAIN,                    // Actor: Anyone responding to "Send Back" (Matrix row 9)
    FORWARD_FOR_REVIEW, // এই লাইনটি যোগ করুন
     // --- Role-Specific Actions ---
    RECOMMEND,                        // Actor: Recommender (Matrix row 4)
    NOT_RECOMMEND,                    // Actor: Recommender (Matrix row 6)
    COORDINATE,

    // --- Approval Actions ---
    APPROVE,                          // Actor: Approver (Matrix row 8)
    NOT_APPROVE,                      // Actor: Approver (Matrix row 7)
    FINAL_APPROVE,                    // Internal system terminal action

    // --- Return / Backward Flow ---
    SEND_BACK_FOR_FURTHER_INFO_ACTION, // Actor: Recommender, Coordinator, Approver (Matrix row 8)

    // --- Verification & Analysis (Keeping for specific roles) ---
    VERIFY,
    NOT_VERIFY,
    ANALYSIS,
    NOT_ANALYSIS,

    // --- Legacy & Internal Tracking (Optional/Cleanup) ---
    POSTPONE,                         // Map to HOLD_OFF
    BACK,                             // Map to SEND_BACK_FOR_FURTHER_INFO_ACTION
    REJECT                            // Map to NOT_APPROVE


//    SUBMIT,
//    REVIEW,
//    VERIFY,
//    ANALYSIS,
//    RECOMMEND,
//    INFO_REQUEST,
//    NOT_RECOMMEND,
//    FINANCIAL_APPROVE,
//    NOT_APPROVED,
//    FINAL_APPROVE,
//    BACK,
//    POSTPONE,
//    WITHDRAW,
//    REJECT,
//    SEND,
//    COORDINATE,
//    APPROVE

}
