package com.cd.recruitment_requisition_service.utils;

import java.util.HashMap;
import java.util.Map;

public class ActionFormatterHelper {


    private static final Map<String, String> ACTION_MAP = new HashMap<>();

    static {
        // Core & Initial
        ACTION_MAP.put("SUBMIT", "Submitted");
        ACTION_MAP.put("WITHDRAW", "Withdrawn");
        ACTION_MAP.put("HOLD_OFF", "Held Off");

        // Forwarding Logic
        ACTION_MAP.put("FORWARD_FOR_RECOMMENDATION", "Forwarded For Recommendation");
      //  ACTION_MAP.put("FORWARD_FOR_FURTHER_REVIEW", "Forwarded For Further Review");
        ACTION_MAP.put("FORWARD_FOR_FURTHER_REVIEW", "Forwarded For Review");
        ACTION_MAP.put("FORWARD_AGAIN", "Forwarded Again");
        ACTION_MAP.put("FORWARD_FOR_REVIEW", "Forwarded For Review");

        // Role-Specific
        ACTION_MAP.put("RECOMMEND", "Recommended");
        ACTION_MAP.put("NOT_RECOMMEND", "Not Recommended");
        ACTION_MAP.put("COORDINATE", "Coordinated");


        // Approval
        ACTION_MAP.put("APPROVE", "Approved");
        ACTION_MAP.put("NOT_APPROVE", "Not Approved");
        ACTION_MAP.put("FINAL_APPROVE", "Final Approved");

        // Return Flow
        ACTION_MAP.put("SEND_BACK_FOR_FURTHER_INFO_ACTION", "Sent Back For Further Info/Action");

        // Verification & Analysis
        ACTION_MAP.put("VERIFY", "Verified");
        ACTION_MAP.put("NOT_VERIFY", "Not Verified");
        ACTION_MAP.put("ANALYSIS", "Analysed");
        ACTION_MAP.put("NOT_ANALYSIS", "Not Analysed");

        // Legacy & Mapping
        ACTION_MAP.put("POSTPONE", "Postponed");
        ACTION_MAP.put("BACK", "Sent Back");
        ACTION_MAP.put("REJECT", "Rejected");
    }

    /**
     * ডাটাবেস এনাম ভ্যালু থেকে প্রফেশনাল রিপোর্ট ফরম্যাট রিটার্ন করে।
     * @param actionEnumName (e.g., "SUBMIT" or action.name())
     * @return Formatted String (e.g., "Submitted")
     */
    public static String getReportActionName(String actionEnumName) {
        // ১. যদি ভ্যালু নাল বা খালি হয়, তবে সরাসরি "" রিটার্ন করবে
        if (actionEnumName == null || actionEnumName.trim().isEmpty()) {
            return "";
        }

        // ২. ম্যাপ থেকে ডাটা খোঁজা
        String formatted = ACTION_MAP.get(actionEnumName.toUpperCase());

        // ৩. যদি ম্যাপে না থাকে তবে ফলব্যাক হিসেবে ফরম্যাট করা (যেমন: REJECT -> Rejected)
        if (formatted == null) {
            return formatDefault(actionEnumName);
        }

        return formatted;
    }

    private static String formatDefault(String text) {
        if (text == null || text.isEmpty()) return "";
        String clean = text.replace("_", " ").toLowerCase();
        return Character.toUpperCase(clean.charAt(0)) + clean.substring(1);
    }
}
