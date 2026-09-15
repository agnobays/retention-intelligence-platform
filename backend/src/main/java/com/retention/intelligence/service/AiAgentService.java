package com.retention.intelligence.service;

import com.retention.intelligence.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAgentService {

    private static final Logger log = LoggerFactory.getLogger(AiAgentService.class);

    @Value("${OPENAI_API_KEY:${openai.api-key:}}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public static class CustomEmailResponse {
        private String subject;
        private String htmlContent;
        private String textSummary;
        private String followUpSchedule;

        public CustomEmailResponse(String subject, String htmlContent, String textSummary, String followUpSchedule) {
            this.subject = subject;
            this.htmlContent = htmlContent;
            this.textSummary = textSummary;
            this.followUpSchedule = followUpSchedule;
        }

        public String getSubject() { return subject; }
        public String getHtmlContent() { return htmlContent; }
        public String getTextSummary() { return textSummary; }
        public String getFollowUpSchedule() { return followUpSchedule; }
    }

    /**
     * Autonomous AI Agent checks the specific customer issue and synthesizes
     * a warm, highly empathetic custom customer care email with voucher/concession details.
     */
    public CustomEmailResponse generateTailoredCustomerEmail(Customer customer) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Customer";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        String segment = customer != null && customer.getCustomerSegment() != null ? customer.getCustomerSegment() : "PRIVATE_CLIENT";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Resolution";
        String severity = customer != null && customer.getIssueSeverity() != null ? customer.getIssueSeverity() : "HIGH";
        int resolutionHours = customer != null && customer.getResolutionTimeHours() != null ? customer.getResolutionTimeHours() : 24;
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Complimentary Concession & Loyalty Reward";

        log.info("🤖 [AI AGENT] Analyzing Customer Issue Telemetry for Client: {} ({})", clientName, extId);
        log.info("  ├─ Customer Issue   : {}", issue);
        log.info("  ├─ Issue Severity   : {} (Processing Time: {}h)", severity, resolutionHours);
        log.info("  └─ Customer Voucher : {}", reward);

        // Attempt external LLM API if key is configured
        if (openAiApiKey != null && !openAiApiKey.trim().isEmpty() && openAiApiKey.startsWith("sk-")) {
            try {
                return callOpenAiForEmail(clientName, extId, segment, issue, severity, resolutionHours, reward);
            } catch (Exception e) {
                log.warn("⚠️ OpenAI API call failed, using built-in AI NLG Synthesizer fallback: {}", e.getMessage());
            }
        }

        // Built-in Intelligent AI Natural Language Synthesizer Engine
        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, reward);
    }

    /**
     * Synthesizes automated follow-up email check-in (24h/48h/72h) while issue is being resolved.
     */
    public CustomEmailResponse generateFollowUpCheckIn(Customer customer, int followUpNumber) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Customer";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Request";
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Complimentary Voucher";

        String subject = String.format("Standard Bank Customer Care Follow-Up: Status Check on Your %s - %s", issue, clientName);
        
        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0b1120; color: #f8fafc; padding: 30px; border-radius: 12px; border: 1px solid #1e293b; max-width: 650px; margin: 0 auto;\">" +
            "  <div style=\"background: linear-gradient(135deg, #1e3a8a, #0284c7); padding: 20px; border-radius: 8px; text-align: center; margin-bottom: 20px;\">" +
            "    <h1 style=\"color: #ffffff; margin: 0; font-size: 22px;\">Standard Bank Customer Executive Desk</h1>" +
            "    <p style=\"color: #93c5fd; margin: 5px 0 0 0; font-size: 14px;\">Service Resolution Check-In #%d | %s</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; line-height: 1.6; color: #e2e8f0;\">Dear <strong>%s</strong>,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.6; color: #cbd5e1;\">" +
            "    We are following up regarding your ongoing request: <strong>\"%s\"</strong>. Our dedicated operations team is still actively working on finalizing this for you to ensure everything is resolved to your exact satisfaction." +
            "  </p>" +
            "  <div style=\"background-color: #1e293b; border-left: 4px solid #38bdf8; padding: 15px; margin: 20px 0; border-radius: 6px;\">" +
            "    <p style=\"margin: 0; font-weight: bold; color: #38bdf8;\">Complimentary Voucher Reminder:</p>" +
            "    <p style=\"margin: 5px 0 0 0; color: #e2e8f0; font-size: 14px;\">As a thank you for your patience while we complete your issue resolution, your gift <strong>%s</strong> remains active for your immediate enjoyment.</p>" +
            "  </div>" +
            "  <p style=\"font-size: 14px; color: #94a3b8;\">If you have any questions or additional requirements while we complete this, simply reply directly to this message or call Senior Relationship Manager Sipho Dlamini.</p>" +
            "  <hr style=\"border: none; border-top: 1px solid #334155; margin: 25px 0;\" />" +
            "  <p style=\"font-size: 12px; color: #64748b; text-align: center;\">Standard Bank Client Executive Desk | Service Reference %s</p>" +
            "</div>",
            followUpNumber, clientName, clientName, issue, reward, extId
        );

        String textSummary = String.format("Warm Customer Care Check-In #%d sent to %s regarding '%s'. Gift reserved: %s", followUpNumber, clientName, issue, reward);
        String schedule = "Next check-in scheduled in 24 hours while issue resolution is finalized.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse synthesizeCustomEmailNlg(String clientName, String extId, String segment, String issue, String severity, int resolutionHours, String reward) {
        String subject;
        String issueProgressMessage;
        String voucherExplanation;
        String partnerPlace;

        if (issue.toLowerCase().contains("investment") || issue.toLowerCase().contains("delay")) {
            subject = String.format("Standard Bank Executive Care: Update on Your Investment Request & Complimentary Gift for %s", clientName);
            issueProgressMessage = String.format("We are contacting you regarding your recent <strong>Investment Transfer Request</strong>. Our Senior Private Banking Operations Team is currently clearing processing queues to finalize your transfer as quickly as possible.", resolutionHours);
            partnerPlace = "fine dining restaurants, luxury spa retreats, and golf estates across South Africa";
            voucherExplanation = String.format("We deeply appreciate your trust in Standard Bank. While our team completes your investment request, we would like you to enjoy a complimentary <strong>%s</strong>, which you can use immediately for %s.", reward, partnerPlace);
        } else if (issue.toLowerCase().contains("settlement") || issue.toLowerCase().contains("merchant") || issue.toLowerCase().contains("disruption")) {
            subject = String.format("Standard Bank Commercial Desk: Priority Update on Your Merchant Settlement - %s", clientName);
            issueProgressMessage = String.format("We are reaching out directly regarding your recent <strong>Merchant Settlement / Payment Disruption</strong>. Our Payment Engineering Operations Desk is actively resolving this to restore 100%% seamless transaction throughput for your business.", resolutionHours);
            partnerPlace = "your daily merchant clearing desk and business operations";
            voucherExplanation = String.format("We know how vital smooth cash flow is for your enterprise. While our engineering team finalizes this for you, we have credited your account with a complimentary <strong>%s</strong> to support your operational cash flow.", reward);
        } else {
            subject = String.format("Standard Bank Customer Care: Status of Your %s & Thank You Gift", issue);
            issueProgressMessage = String.format("Our Customer Care Team is currently working on resolving your recent query regarding <strong>\"%s\"</strong>. We are expediting this with our senior resolution specialists to ensure full completion.", issue);
            partnerPlace = "Woolworths, Pick n Pay, Checkers, and NetFlorist";
            voucherExplanation = String.format("We sincerely thank you for your patience while we resolve this for you. As a token of our appreciation, please accept a complimentary <strong>%s</strong> that you can redeem immediately at partner stores including %s.", reward, partnerPlace);
        }

        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; color: #f1f5f9; padding: 35px; border-radius: 12px; border: 1px solid #1e293b; max-width: 680px; margin: 0 auto;\">" +
            "  <div style=\"background: linear-gradient(135deg, #0284c7, #1d4ed8); padding: 25px; border-radius: 10px; text-align: center; margin-bottom: 25px;\">" +
            "    <h2 style=\"color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 0.5px;\">Standard Bank Executive Care Desk</h2>" +
            "    <p style=\"color: #e0f2fe; margin: 6px 0 0 0; font-size: 15px; font-weight: 500;\">Dedicated Client Service & Resolution Update</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; color: #f8fafc; font-weight: 600;\">Dear %s,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <div style=\"background: linear-gradient(135deg, #1e1b4b, #311b92); border: 1px solid #6366f1; border-radius: 10px; padding: 20px; margin: 25px 0;\">" +
            "    <div style=\"display: flex; align-items: center; margin-bottom: 10px;\">" +
            "      <span style=\"font-size: 22px; margin-right: 10px;\">🎁</span>" +
            "      <h3 style=\"color: #a5b4fc; margin: 0; font-size: 18px;\">Complimentary Gift for Your Patience</h3>" +
            "    </div>" +
            "    <p style=\"color: #ffffff; font-size: 16px; font-weight: bold; margin: 8px 0 0 0;\">%s</p>" +
            "    <p style=\"color: #c7d2fe; font-size: 13px; margin: 6px 0 0 0;\">This gift voucher has been automatically activated for you (Ref: %s).</p>" +
            "  </div>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">Our team will notify you the moment your request is fully resolved. If you have any questions in the meantime, please contact Senior Relationship Manager Sipho Dlamini directly.</p>" +
            "  <div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #334155; text-align: center; color: #64748b; font-size: 12px;\">" +
            "    <p style=\"margin: 0;\">Standard Bank Corporate & Investment Banking | Client Executive Care Desk</p>" +
            "    <p style=\"margin: 4px 0 0 0;\">Ref ID: %s | Priority Customer Service Track</p>" +
            "  </div>" +
            "</div>",
            clientName, issueProgressMessage, voucherExplanation, reward, extId, extId
        );

        String textSummary = String.format("Warm Custom AI Email generated for %s addressing '%s'. Gift: %s", clientName, issue, reward);
        String schedule = "Follow-up #1 scheduled in 24 hours while issue resolution is in progress.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse callOpenAiForEmail(String clientName, String extId, String segment, String issue, String severity, int resolutionHours, String reward) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        String prompt = String.format(
            "Write a warm, highly professional, empathetic executive customer care email from Standard Bank to customer '%s' (ID: %s, Segment: %s). " +
            "Explain that Standard Bank is currently working on resolving their issue '%s' (severity: %s). " +
            "While they wait for the issue to be fully resolved, offer them a complimentary gift voucher: '%s' that they can use immediately at partner lifestyle venues or stores. " +
            "Do NOT mention any retention engine, churn algorithm, or internal metrics. Keep tone warm, executive, and caring.",
            clientName, extId, segment, issue, severity, reward
        );

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(message));
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<?, ?> response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", request, Map.class);

        if (response != null && response.containsKey("choices")) {
            List<?> choices = (List<?>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> msg = (Map<?, ?>) choice.get("message");
                String content = (String) msg.get("content");

                String subject = String.format("Standard Bank Executive Care: Update on Your Request for %s", clientName);
                return new CustomEmailResponse(subject, content, "Generated via OpenAI Executive Care Engine", "Follow-up #1 in 24 hours");
            }
        }

        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, reward);
    }
}
