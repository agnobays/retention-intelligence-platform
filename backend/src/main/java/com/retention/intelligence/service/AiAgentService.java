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
     * a tailored, highly empathetic custom email with voucher/concession details.
     */
    public CustomEmailResponse generateTailoredCustomerEmail(Customer customer) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Corporate Client";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        String segment = customer != null && customer.getCustomerSegment() != null ? customer.getCustomerSegment() : "PRIVATE_CLIENT";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Delay (Investment Request)";
        String severity = customer != null && customer.getIssueSeverity() != null ? customer.getIssueSeverity() : "HIGH";
        int resolutionHours = customer != null && customer.getResolutionTimeHours() != null ? customer.getResolutionTimeHours() : 72;
        int frustration = customer != null && customer.getFrustrationScore() != null ? customer.getFrustrationScore() : 88;
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "R1,500 Lifestyle Experience Voucher & Dedicated Private Banker";

        log.info("🤖 [AI RETENTION AGENT] Analyzing Issue Telemetry for Client: {} ({})", clientName, extId);
        log.info("  ├─ Issue Category   : {}", issue);
        log.info("  ├─ Issue Severity   : {} (Delay: {}h)", severity, resolutionHours);
        log.info("  ├─ Frustration Index: {}/100", frustration);
        log.info("  └─ Concession Reward: {}", reward);

        // Attempt external LLM API if key is configured
        if (openAiApiKey != null && !openAiApiKey.trim().isEmpty() && openAiApiKey.startsWith("sk-")) {
            try {
                return callOpenAiForEmail(clientName, extId, segment, issue, severity, resolutionHours, frustration, reward);
            } catch (Exception e) {
                log.warn("⚠️ OpenAI API call failed, using built-in AI NLG Synthesizer engine fallback: {}", e.getMessage());
            }
        }

        // Built-in Intelligent AI Natural Language Synthesizer Engine
        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, frustration, reward);
    }

    /**
     * Synthesizes automated follow-up email check-in (24h/48h/72h) until ticket is closed/SAVED.
     */
    public CustomEmailResponse generateFollowUpCheckIn(Customer customer, int followUpNumber) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Client";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Resolution";
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Loyalty Concession";

        String subject = String.format("Standard Bank Retention Follow-Up #%d: Status Check on Your %s - %s", followUpNumber, issue, clientName);
        
        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0b1120; color: #f8fafc; padding: 30px; border-radius: 12px; border: 1px solid #1e293b;\">" +
            "  <div style=\"background: linear-gradient(135deg, #1e3a8a, #0284c7); padding: 20px; border-radius: 8px; text-align: center; margin-bottom: 20px;\">" +
            "    <h1 style=\"color: #ffffff; margin: 0; font-size: 22px;\">Standard Bank Autonomous Executive Retention Agent</h1>" +
            "    <p style=\"color: #93c5fd; margin: 5px 0 0 0; font-size: 14px;\">Automated Ticket Follow-Up #%d | Client: %s (%s)</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; line-height: 1.6; color: #e2e8f0;\">Dear <strong>%s</strong>,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.6; color: #cbd5e1;\">" +
            "    This is an automated follow-up from the Standard Bank Retention Executive Desk. Our system is actively tracking your ticket regarding <strong>\"%s\"</strong> to ensure complete resolution." +
            "  </p>" +
            "  <div style=\"background-color: #1e293b; border-left: 4px solid #38bdf8; padding: 15px; margin: 20px 0; border-radius: 4px;\">" +
            "    <p style=\"margin: 0; font-weight: bold; color: #38bdf8;\">Retention Ticket Status Update:</p>" +
            "    <p style=\"margin: 5px 0 0 0; color: #94a3b8; font-size: 14px;\">Your dedicated concession <strong>%s</strong> has been reserved. Please confirm if your operational requirements have been fully satisfied so we may finalize your ticket closure.</p>" +
            "  </div>" +
            "  <p style=\"font-size: 14px; color: #94a3b8;\">If you need further adjustments, simply reply directly to this message or contact your Relationship Manager (Sipho Dlamini).</p>" +
            "  <hr style=\"border: none; border-top: 1px solid #334155; margin: 25px 0;\" />" +
            "  <p style=\"font-size: 12px; color: #64748b; text-align: center;\">Standard Bank Corporate & Investment Banking | Retention Intelligence Engine v3.4</p>" +
            "</div>",
            followUpNumber, clientName, extId, clientName, issue, reward
        );

        String textSummary = String.format("Automated Follow-Up #%d sent to %s regarding '%s'. Reward reserved: %s", followUpNumber, clientName, issue, reward);
        String schedule = "Next check-in scheduled in 24 hours if ticket remains open.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse synthesizeCustomEmailNlg(String clientName, String extId, String segment, String issue, String severity, int resolutionHours, int frustration, String reward) {
        String subject = String.format("Standard Bank Personalized Retention Resolution for %s (%s)", clientName, extId);

        String specificApologyHeader;
        String issueImpactDescription;
        String actionPlan;

        if (issue.toLowerCase().contains("investment") || issue.toLowerCase().contains("delay")) {
            specificApologyHeader = "Urgent Resolution & Formal Apology: Investment Request Delay";
            issueImpactDescription = String.format("Our executive monitoring system flagged a delay of <strong>%d hours</strong> in processing your investment transfer. We recognize that for a %s client of your caliber, timely execution is paramount.", resolutionHours, segment.replace("_", " "));
            actionPlan = "Our Treasury Operations team has personally cleared the processing queue, and your Senior Relationship Manager (Sipho Dlamini) has been assigned to oversee your portfolio directly.";
        } else if (issue.toLowerCase().contains("settlement") || issue.toLowerCase().contains("merchant") || issue.toLowerCase().contains("disruption")) {
            specificApologyHeader = "Priority Resolution: Merchant Settlement & Payment Disruption";
            issueImpactDescription = String.format("We identified a <strong>%d-hour disruption</strong> impacting your daily merchant settlements and cash flow operations. We understand the critical operational impact this causes for enterprise logistics and payments.", resolutionHours);
            actionPlan = "Our Payment Engineering Desk has restored 100% processing throughput, and we have established a dedicated priority desk route for all your enterprise accounts.";
        } else {
            specificApologyHeader = "Personalized Service Recovery & Compensation";
            issueImpactDescription = String.format("Our Retention Intelligence Engine registered an elevated frustration signal (%d/100) regarding your recent service interaction <strong>\"%s\"</strong>.", frustration, issue);
            actionPlan = "We have expedited ticket escalation with senior operational management to ensure immediate resolution.";
        }

        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; color: #f1f5f9; padding: 35px; border-radius: 12px; border: 1px solid #1e293b; max-width: 680px; margin: 0 auto;\">" +
            "  <div style=\"background: linear-gradient(135deg, #0284c7, #1d4ed8); padding: 25px; border-radius: 10px; text-align: center; margin-bottom: 25px;\">" +
            "    <h2 style=\"color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 0.5px;\">Standard Bank CIB</h2>" +
            "    <p style=\"color: #e0f2fe; margin: 6px 0 0 0; font-size: 15px; font-weight: 500;\">%s</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; color: #f8fafc; font-weight: 600;\">Dear %s,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <div style=\"background: linear-gradient(135deg, #1e1b4b, #311b92); border: 1px solid #6366f1; border-radius: 10px; padding: 20px; margin: 25px 0;\">" +
            "    <div style=\"display: flex; align-items: center; margin-bottom: 10px;\">" +
            "      <span style=\"font-size: 20px; margin-right: 10px;\">🎁</span>" +
            "      <h3 style=\"color: #a5b4fc; margin: 0; font-size: 18px;\">Tailored Loyalty Compensation Offer</h3>" +
            "    </div>" +
            "    <p style=\"color: #ffffff; font-size: 16px; font-weight: bold; margin: 8px 0 0 0;\">%s</p>" +
            "    <p style=\"color: #c7d2fe; font-size: 13px; margin: 6px 0 0 0;\">This concession has been automatically activated on your profile (Ref: %s).</p>" +
            "  </div>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">Our Autonomous AI Retention Desk will monitor your ticket until resolution is verified. Should you require immediate assistance, please contact Senior Relationship Manager Sipho Dlamini directly.</p>" +
            "  <div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #334155; text-align: center; color: #64748b; font-size: 12px;\">" +
            "    <p style=\"margin: 0;\">Standard Bank Corporate & Investment Banking | Autonomous AI Retention Engine</p>" +
            "    <p style=\"margin: 4px 0 0 0;\">Ref ID: %s | High Priority Resolution Track</p>" +
            "  </div>" +
            "</div>",
            specificApologyHeader, clientName, issueImpactDescription, actionPlan, reward, extId, extId
        );

        String textSummary = String.format("Custom AI Email generated for %s addressing issue '%s' (%d/100 frustration). Concession: %s", clientName, issue, frustration, reward);
        String schedule = "Follow-up #1 scheduled in 24 hours. AI Agent will auto-close ticket upon customer acknowledgment.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse callOpenAiForEmail(String clientName, String extId, String segment, String issue, String severity, int resolutionHours, int frustration, String reward) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        String prompt = String.format(
            "Write a highly professional, empathetic, custom retention email from Standard Bank CIB to customer '%s' (ID: %s, Segment: %s). " +
            "The customer experienced issue '%s' with severity %s causing a delay of %d hours and a frustration score of %d/100. " +
            "Offer them the following tailored loyalty reward: '%s'. Keep tone warm, corporate, and executive. Return plain text HTML.",
            clientName, extId, segment, issue, severity, resolutionHours, frustration, reward
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

                String subject = String.format("Standard Bank Tailored Resolution for %s (%s)", clientName, extId);
                return new CustomEmailResponse(subject, content, "Generated via OpenAI LLM Engine", "Follow-up #1 in 24 hours");
            }
        }

        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, frustration, reward);
    }
}
