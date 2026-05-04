package com.cd.recruitment_requisition_service.enums;

public enum RequisitionStatus {


    // --- Initial State ---
    DRAFT,               // Initiator ড্রাফট হিসেবে সেভ করে রেখেছে

    // --- Active Processing States (ফাইলটি বর্তমানে কারো ডেস্কে আছে) ---
    SUBMITTED,           // প্রথমবার সাবমিট করা হয়েছে
    UNDER_REVIEW,        // রিভিউয়ারের ডেস্কে আছে
    UNDER_VERIFICATION,  // Verifier-এর ডেস্কে আছে
    UNDER_ANALYSIS,      // Analyst-এর ডেস্কে আছে
    UNDER_RECOMMENDATION, // Recommender-এর ডেস্কে আছে (ম্যাট্রিক্স অনুযায়ী নতুন)
    UNDER_COORDINATION,   // Coordinator-এর ডেস্কে আছে (ম্যাট্রিক্স অনুযায়ী নতুন)
    UNDER_APPROVAL,       // Approver-এর ডেস্কে আছে

    // --- Intermediate Passed States (সফলভাবে কোনো স্টেজ পার করেছে) ---
    VERIFIED,            // Verifier সফলভাবে যাচাই করেছে
    ANALYZED,            // Analyst সফলভাবে অ্যানালাইসিস করেছে
    COORDINATED,         // Coordinator ফরওয়ার্ড করেছে
    RECOMMENDED,         // Recommender সুপারিশ করেছে

    // --- Backward & Hold States ---
    BACKED,              // "Send Back for Further Info/Action" করা হয়েছে
    POSTPONED,           // "Hold off" করা হয়েছে (Matrix অনুযায়ী)
    INFO_REQUESTED,      // লিগ্যাসি ব্যাক-ফ্লো

    // --- Terminal States (প্রসেসটি পুরোপুরি শেষ) ---
    FINAL_APPROVED,      // Approver চূড়ান্ত অনুমোদন দিয়েছে
    REJECTED,            // সরাসরি বাতিল করা হয়েছে
    WITHDRAWN,           // Initiator রিকুইজিশন তুলে নিয়েছে

    // --- Negative Processing Statuses (প্রসেস এখনো শেষ হয়নি, বাট নেগেটিভ ফিডব্যাক) ---
    NOT_RECOMMENDED,     // Recommender সুপারিশ করেনি (Matrix row 6)
    NOT_ANALYZED,        // Analyst নেগেটিভ রিপোর্ট দিয়েছে
    NOT_VERIFIED,        // Verifier তথ্য সঠিক পায়নি
    NOT_APPROVED         // Approver অনুমোদন দেয়নি (Matrix row 7)

//    DRAFT,
//    SUBMITTED,
//    UNDER_REVIEW,
//    UNDER_VERIFICATION, //  Verification desk-e thakle
//    UNDER_ANALYSIS,     //  Analysis desk-e thakle
//    INFO_REQUESTED,
//    RECOMMENDED,
//    NOT_RECOMMENDED,    //  Jodi keu recommend na kore forward kore
//    COORDINATED,        //  Coordinator level par hole
//    FINANCE_APPROVED,
//    FINAL_APPROVED,
//    BACKED,
//    POSTPONED,
//    WITHDRAWN,
//    NOT_APPROVED,
//    REJECTED

}
