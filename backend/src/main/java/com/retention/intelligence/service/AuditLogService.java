package com.retention.intelligence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    public static class CustomerJourneyLogItem {
        private String sessionId;
        private String externalCustomerId;
        private String customerName;
        private String eventType; // e.g. DETECTION_FLAGGED, MANAGER_APPROVED, EMAIL_DISPATCHED, AI_CHECKIN, CASE_SAVED
        private String timestamp;
        private String approver; // e.g. "Sipho Dlamini (Senior CIB RM)" or "Autonomous AI Executive Desk"
        private String recipientEmail;
        private String subject;
        private String messageContent;
        private String concessionReward;

        public CustomerJourneyLogItem() {}

        public CustomerJourneyLogItem(String sessionId, String externalCustomerId, String customerName, String eventType, 
                                      String timestamp, String approver, String recipientEmail, String subject, 
                                      String messageContent, String concessionReward) {
            this.sessionId = sessionId;
            this.externalCustomerId = externalCustomerId;
            this.customerName = customerName;
            this.eventType = eventType;
            this.timestamp = timestamp;
            this.approver = approver;
            this.recipientEmail = recipientEmail;
            this.subject = subject;
            this.messageContent = messageContent;
            this.concessionReward = concessionReward;
        }

        public String getSessionId() { return sessionId; }
        public String getExternalCustomerId() { return externalCustomerId; }
        public String getCustomerName() { return customerName; }
        public String getEventType() { return eventType; }
        public String getTimestamp() { return timestamp; }
        public String getApprover() { return approver; }
        public String getRecipientEmail() { return recipientEmail; }
        public String getSubject() { return subject; }
        public String getMessageContent() { return messageContent; }
        public String getConcessionReward() { return concessionReward; }
    }

    private final List<CustomerJourneyLogItem> journeyLogs = Collections.synchronizedList(new ArrayList<>());

    public AuditLogService() {
        // Initialize default seed audit logs for demonstration
        recordLog(
            "SESS-20260915-A8F29C",
            "SB-PC-001",
            "Dr. Anele Nkosi (Private Client)",
            "RETENTION_EMAIL_DISPATCHED",
            "Sipho Dlamini (Senior CIB Relationship Manager)",
            "zolani1999@gmail.com",
            "Standard Bank Executive Care: Update on Your Investment Request & Complimentary Gift",
            "Dear Dr. Anele Nkosi, We are contacting you directly regarding your recent Investment Request Service Delay. Our Senior Private Banking Operations Team is currently clearing processing queues to finalize your transfer. While our team completes this for you, please enjoy a complimentary R1,500 Lifestyle Experience Voucher & Dedicated Private Banker Access.",
            "R1,500 Lifestyle Experience Voucher & Dedicated Private Banker"
        );

        recordLog(
            "SESS-20260915-B739E1",
            "SB-CC-002",
            "Apex Logistics Enterprise (SME)",
            "RETENTION_EMAIL_DISPATCHED",
            "Autonomous AI Executive Desk",
            "vgnobookings@gmail.com",
            "Standard Bank Commercial Desk: Priority Update on Your Merchant Settlement",
            "Dear Apex Logistics Enterprise, We are reaching out directly regarding your recent Merchant Settlement / Payment Disruption. Our Payment Engineering Operations Desk is actively resolving this to restore 100% seamless transaction throughput for your business. We have credited your account with a complimentary Merchant Fee Waiver & Priority Settlement Desk Access.",
            "Merchant Fee Waiver & Priority Settlement Desk Access"
        );

        recordLog(
            "SESS-20260915-C948F2",
            "SB-EC-003",
            "Thabo Khumalo (Everyday Banking)",
            "RETENTION_EMAIL_DISPATCHED",
            "Thabo Mokoena (Customer Care Specialist)",
            "uunderratedrecords@gmail.com",
            "Standard Bank Customer Care: Status of Your Card Dispute & Thank You Gift",
            "Dear Thabo Khumalo, Our Customer Care Team is currently working on resolving your recent query regarding Card Transaction Dispute SLA Dissatisfaction. As a token of our appreciation for your patience while we resolve this for you, please accept 5,000 Loyalty Bonus Points / R250 Retail Voucher redeemable at Woolworths, Pick n Pay, or Checkers.",
            "5,000 Loyalty Bonus Points / R250 Retail Voucher"
        );
    }

    public CustomerJourneyLogItem recordLog(String sessionId, String externalCustomerId, String customerName, 
                                            String eventType, String approver, String recipientEmail, 
                                            String subject, String messageContent, String concessionReward) {

        String activeSession = (sessionId != null && !sessionId.isEmpty()) 
            ? sessionId 
            : "SESS-" + System.currentTimeMillis() % 1000000;
        
        String timeStr = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        CustomerJourneyLogItem item = new CustomerJourneyLogItem(
            activeSession,
            externalCustomerId != null ? externalCustomerId : "SB-CIB-1001",
            customerName != null ? customerName : "Standard Bank Client",
            eventType != null ? eventType : "SYSTEM_EVENT",
            timeStr,
            approver != null ? approver : "Autonomous AI Executive Desk",
            recipientEmail != null ? recipientEmail : "client@standardbank.co.za",
            subject != null ? subject : "Standard Bank Customer Resolution Notice",
            messageContent != null ? messageContent : "Service update in progress.",
            concessionReward != null ? concessionReward : "Standard Loyalty Concession"
        );

        journeyLogs.add(0, item); // Newest logs first

        // Console Audit Banner Output
        log.info("================================================================================");
        log.info("📜 [CUSTOMER JOURNEY AUDIT LOG RECORDED]");
        log.info("  ├─ SESSION ID        : {}", item.getSessionId());
        log.info("  ├─ CUSTOMER NUMBER   : {}", item.getExternalCustomerId());
        log.info("  ├─ CUSTOMER NAME     : {}", item.getCustomerName());
        log.info("  ├─ EVENT TYPE        : {}", item.getEventType());
        log.info("  ├─ TIMESTAMP         : {}", item.getTimestamp());
        log.info("  ├─ APPROVED BY       : {}", item.getApprover());
        log.info("  ├─ RECIPIENT INBOX   : {}", item.getRecipientEmail());
        log.info("  ├─ EMAIL SUBJECT     : {}", item.getSubject());
        log.info("  ├─ CONCESSION OFFER  : {}", item.getConcessionReward());
        log.info("  └─ FULL MESSAGE SENT : \"{}\"", item.getMessageContent());
        log.info("================================================================================");

        return item;
    }

    public List<CustomerJourneyLogItem> getAllJourneyLogs() {
        return new ArrayList<>(journeyLogs);
    }

    public List<CustomerJourneyLogItem> getLogsByCustomer(String externalCustomerId) {
        List<CustomerJourneyLogItem> filtered = new ArrayList<>();
        for (CustomerJourneyLogItem item : journeyLogs) {
            if (item.getExternalCustomerId() != null && item.getExternalCustomerId().equalsIgnoreCase(externalCustomerId)) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}
