package com.retention.intelligence.service;

import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.RetentionPolicy;
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

    public CustomEmailResponse generateTailoredCustomerEmail(Customer customer) {
        return generateTailoredCustomerEmail(customer, null);
    }

    public CustomEmailResponse generateFollowUpCheckIn(Customer customer, int followUpNumber) {
        return generateFollowUpCheckIn(customer, followUpNumber, null);
    }

    /**
     * Autonomous AI Agent checks the specific customer issue and synthesizes
     * a warm, highly empathetic custom customer care email with voucher/concession details,
     * branded dynamically to the tenant's business identity.
     */
    public CustomEmailResponse generateTailoredCustomerEmail(Customer customer, RetentionPolicy policy) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Customer";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "CUST-001";
        String segment = customer != null && customer.getCustomerSegment() != null ? customer.getCustomerSegment() : "COMMERCIAL_SME";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Resolution";
        String severity = customer != null && customer.getIssueSeverity() != null ? customer.getIssueSeverity() : "HIGH";
        int resolutionHours = customer != null && customer.getResolutionTimeHours() != null ? customer.getResolutionTimeHours() : 24;
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Complimentary Concession & Loyalty Reward";

        String brandName = resolveBrandName(customer, policy);
        String senderName = resolveSenderName(policy);
        String senderTitle = resolveSenderTitle(policy);
        String senderEmail = resolveSenderEmail(policy);
        String businessType = resolveBusinessType(customer, policy);

        log.info("🤖 [AI AGENT] Analyzing Customer Telemetry for Brand: '{}' | Client: {} ({})", brandName, clientName, extId);
        log.info("  ├─ Issue: {} (Severity: {}, SLA: {}h)", issue, severity, resolutionHours);
        log.info("  └─ Reward/Concession: {}", reward);

        // Attempt external LLM API if key is configured
        if (openAiApiKey != null && !openAiApiKey.trim().isEmpty() && openAiApiKey.startsWith("sk-")) {
            try {
                return callOpenAiForEmail(clientName, extId, segment, issue, severity, resolutionHours, reward, brandName, businessType);
            } catch (Exception e) {
                log.warn("⚠️ OpenAI API call failed, using built-in AI NLG Synthesizer fallback: {}", e.getMessage());
            }
        }

        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, reward, brandName, senderName, senderTitle, senderEmail, businessType);
    }

    public CustomEmailResponse generateFollowUpCheckIn(Customer customer, int followUpNumber, RetentionPolicy policy) {
        String clientName = customer != null && customer.getName() != null ? customer.getName() : "Valued Customer";
        String extId = customer != null && customer.getExternalCustomerId() != null ? customer.getExternalCustomerId() : "CUST-001";
        String issue = customer != null && customer.getIssueCategory() != null ? customer.getIssueCategory() : "Service Request";
        String reward = customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Complimentary Voucher";

        String brandName = resolveBrandName(customer, policy);
        String senderName = resolveSenderName(policy);

        String subject = String.format("%s: Follow-Up Status Check on Your %s - %s", brandName, issue, clientName);

        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0b1120; color: #f8fafc; padding: 30px; border-radius: 12px; border: 1px solid #1e293b; max-width: 650px; margin: 0 auto;\">" +
            "  <div style=\"background: linear-gradient(135deg, #1e3a8a, #0284c7); padding: 20px; border-radius: 8px; text-align: center; margin-bottom: 20px;\">" +
            "    <h1 style=\"color: #ffffff; margin: 0; font-size: 22px;\">%s</h1>" +
            "    <p style=\"color: #93c5fd; margin: 5px 0 0 0; font-size: 14px;\">Resolution Check-In #%d | %s</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; line-height: 1.6; color: #e2e8f0;\">Dear <strong>%s</strong>,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.6; color: #cbd5e1;\">" +
            "    We are following up regarding your ongoing query: <strong>\"%s\"</strong>. Our dedicated team is actively finalizing this to ensure complete satisfaction." +
            "  </p>" +
            "  <div style=\"background-color: #1e293b; border-left: 4px solid #38bdf8; padding: 15px; margin: 20px 0; border-radius: 6px;\">" +
            "    <p style=\"margin: 0; font-weight: bold; color: #38bdf8;\">Complimentary Reward Reminder:</p>" +
            "    <p style=\"margin: 5px 0 0 0; color: #e2e8f0; font-size: 14px;\">As a token of appreciation for your patience, your concession <strong>%s</strong> is ready for your immediate use.</p>" +
            "  </div>" +
            "  <p style=\"font-size: 14px; color: #94a3b8;\">If you need immediate assistance, reply directly to this message or contact %s.</p>" +
            "  <hr style=\"border: none; border-top: 1px solid #334155; margin: 25px 0;\" />" +
            "  <p style=\"font-size: 12px; color: #64748b; text-align: center;\">%s | Reference ID %s</p>" +
            "</div>",
            brandName, followUpNumber, clientName, clientName, issue, reward, senderName, brandName, extId
        );

        String textSummary = String.format("Check-In #%d sent to %s regarding '%s'. Concession: %s", followUpNumber, clientName, issue, reward);
        String schedule = "Next check-in scheduled in 24 hours.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse synthesizeCustomEmailNlg(
            String clientName, String extId, String segment, String issue, String severity,
            int resolutionHours, String reward, String brandName, String senderName,
            String senderTitle, String senderEmail, String businessType) {

        String subject;
        String issueProgressMessage;
        String voucherExplanation;

        String issueLower = issue.toLowerCase();

        if (issueLower.contains("delivery") || issueLower.contains("fleet") || issueLower.contains("truck") || issueLower.contains("breakdown") || issueLower.contains("cold chain")) {
            // Logistics & Cold Chain domain
            subject = String.format("%s: Priority Logistics Resolution & Fleet Credit for %s", brandName, clientName);
            issueProgressMessage = String.format("We are contacting you directly regarding the recent delivery dispatch query: <strong>\"%s\"</strong>. Our Logistics & Fleet Dispatch Engineering Desk is expediting priority rerouting and cold-chain integrity to resolve this.", issue);
            voucherExplanation = String.format("We understand how critical smooth operations and on-time shipments are for your business. As our commitment to your partnership, we have activated a complimentary <strong>%s</strong> for your account.", reward);
        } else if (issueLower.contains("investment") || issueLower.contains("delay") || issueLower.contains("transfer")) {
            // Financial investment
            subject = String.format("%s: Update on Your Request & Executive Concession for %s", brandName, clientName);
            issueProgressMessage = String.format("We are contacting you regarding your recent request: <strong>\"%s\"</strong>. Our Senior Operations Team is clearing processing queues to finalize this as quickly as possible.", issue);
            voucherExplanation = String.format("We deeply appreciate your trust in our organization. While our team completes this for you, please accept a complimentary <strong>%s</strong>.", reward);
        } else if (issueLower.contains("settlement") || issueLower.contains("merchant") || issueLower.contains("disruption")) {
            // Payment / merchant settlement
            subject = String.format("%s: Priority Status on Merchant Settlement - %s", brandName, clientName);
            issueProgressMessage = String.format("We are reaching out regarding your recent inquiry: <strong>\"%s\"</strong>. Our Engineering Operations Desk is actively resolving this to restore 100%% seamless throughput.", issue);
            voucherExplanation = String.format("We know how vital smooth cash flow and continuity are for your enterprise. While our engineering team finalizes this, we have credited your account with: <strong>%s</strong>.", reward);
        } else {
            // General business retention
            subject = String.format("%s: Service Resolution Update & Courtesy Credit for %s", brandName, clientName);
            issueProgressMessage = String.format("Our Customer Care Team is currently resolving your query regarding: <strong>\"%s\"</strong>. We are expediting this with our senior specialists.", issue);
            voucherExplanation = String.format("We sincerely value your relationship and apologize for the inconvenience. As a token of our appreciation, please enjoy a complimentary <strong>%s</strong>.", reward);
        }

        String htmlContent = String.format(
            "<div style=\"font-family: Arial, sans-serif; background-color: #0f172a; color: #f1f5f9; padding: 35px; border-radius: 12px; border: 1px solid #1e293b; max-width: 680px; margin: 0 auto;\">" +
            "  <div style=\"background: linear-gradient(135deg, #0284c7, #1d4ed8); padding: 25px; border-radius: 10px; text-align: center; margin-bottom: 25px;\">" +
            "    <h2 style=\"color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 0.5px;\">%s</h2>" +
            "    <p style=\"color: #e0f2fe; margin: 6px 0 0 0; font-size: 15px; font-weight: 500;\">Priority Customer Service & Resolution Desk</p>" +
            "  </div>" +
            "  <p style=\"font-size: 16px; color: #f8fafc; font-weight: 600;\">Dear %s,</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">%s</p>" +
            "  <div style=\"background: linear-gradient(135deg, #1e1b4b, #311b92); border: 1px solid #6366f1; border-radius: 10px; padding: 20px; margin: 25px 0;\">" +
            "    <div style=\"display: flex; align-items: center; margin-bottom: 10px;\">" +
            "      <span style=\"font-size: 22px; margin-right: 10px;\">🎁</span>" +
            "      <h3 style=\"color: #a5b4fc; margin: 0; font-size: 18px;\">Complimentary Retention Benefit Activated</h3>" +
            "    </div>" +
            "    <p style=\"color: #ffffff; font-size: 16px; font-weight: bold; margin: 8px 0 0 0;\">%s</p>" +
            "    <p style=\"color: #c7d2fe; font-size: 13px; margin: 6px 0 0 0;\">Ref: %s | Automatically registered to your corporate account.</p>" +
            "  </div>" +
            "  <p style=\"font-size: 15px; line-height: 1.7; color: #cbd5e1;\">Our team will notify you the moment your request is fully resolved. If you have any questions, please contact %s (%s) directly at %s.</p>" +
            "  <div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #334155; text-align: center; color: #64748b; font-size: 12px;\">" +
            "    <p style=\"margin: 0;\">%s</p>" +
            "    <p style=\"margin: 4px 0 0 0;\">Account Ref: %s | Priority Retention & Care Track</p>" +
            "  </div>" +
            "</div>",
            brandName, clientName, issueProgressMessage, voucherExplanation, reward, extId,
            senderName, senderTitle, senderEmail, brandName, extId
        );

        String textSummary = String.format("AI Retention Email synthesized for %s (%s) regarding '%s'. Reward: %s", clientName, brandName, issue, reward);
        String schedule = "Follow-up #1 scheduled in 24 hours.";

        return new CustomEmailResponse(subject, htmlContent, textSummary, schedule);
    }

    private CustomEmailResponse callOpenAiForEmail(
            String clientName, String extId, String segment, String issue, String severity,
            int resolutionHours, String reward, String brandName, String businessType) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        String prompt = String.format(
            "Write a warm, empathetic, executive customer care email from '%s' (Industry: %s) to customer '%s' (ID: %s, Segment: %s). " +
            "Explain that the company is currently prioritizing resolution of their issue '%s' (severity: %s). " +
            "Offer them a complimentary retention reward/concession: '%s' to support them while resolution is completed. " +
            "Do NOT mention any churn score, algorithm, or internal metrics. Keep tone warm, professional, and dedicated.",
            brandName, businessType, clientName, extId, segment, issue, severity, reward
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
                String subject = String.format("%s: Service Resolution Update for %s", brandName, clientName);
                return new CustomEmailResponse(subject, content, "Generated via OpenAI Care Engine", "Follow-up in 24 hours");
            }
        }

        return synthesizeCustomEmailNlg(clientName, extId, segment, issue, severity, resolutionHours, reward, brandName, "Customer Director", "Head of Client Relations", "care@" + brandName.toLowerCase().replace(" ", "") + ".co.za", businessType);
    }

    private String resolveBrandName(Customer customer, RetentionPolicy policy) {
        if (policy != null && policy.getBrandName() != null && !policy.getBrandName().isBlank()) {
            return policy.getBrandName();
        }
        if (customer != null && customer.getCompany() != null && customer.getCompany().getName() != null) {
            return customer.getCompany().getName() + " Executive Care Desk";
        }
        return "Customer Executive Care Desk";
    }

    private String resolveSenderName(RetentionPolicy policy) {
        if (policy != null && policy.getSenderName() != null && !policy.getSenderName().isBlank()) {
            return policy.getSenderName();
        }
        return "Operations Director";
    }

    private String resolveSenderTitle(RetentionPolicy policy) {
        if (policy != null && policy.getSenderTitle() != null && !policy.getSenderTitle().isBlank()) {
            return policy.getSenderTitle();
        }
        return "Senior Client Relations Officer";
    }

    private String resolveSenderEmail(RetentionPolicy policy) {
        if (policy != null && policy.getSenderEmail() != null && !policy.getSenderEmail().isBlank()) {
            return policy.getSenderEmail();
        }
        return "operations@retentionplatform.com";
    }

    private String resolveBusinessType(Customer customer, RetentionPolicy policy) {
        if (policy != null && policy.getBusinessType() != null) {
            return policy.getBusinessType();
        }
        if (customer != null && customer.getCompany() != null && customer.getCompany().getIndustry() != null) {
            return customer.getCompany().getIndustry();
        }
        return "GENERAL";
    }

    public static class SentimentResponse {
        private String detectedEmotion;
        private int sentimentScore;
        private int frustrationAdjustment;

        public SentimentResponse(String detectedEmotion, int sentimentScore, int frustrationAdjustment) {
            this.detectedEmotion = detectedEmotion;
            this.sentimentScore = sentimentScore;
            this.frustrationAdjustment = frustrationAdjustment;
        }

        public String getDetectedEmotion() { return detectedEmotion; }
        public int getSentimentScore() { return sentimentScore; }
        public int getFrustrationAdjustment() { return frustrationAdjustment; }
    }

    public SentimentResponse analyzeSentiment(String text) {
        if (openAiApiKey != null && !openAiApiKey.trim().isEmpty() && openAiApiKey.startsWith("sk-")) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(openAiApiKey);

                String prompt = "Analyze the sentiment of the following customer message. " +
                        "Return exactly a JSON object with three keys: 'detectedEmotion' (string, e.g. 'Furious', 'Satisfied'), " +
                        "'sentimentScore' (integer from -100 to 100), and 'frustrationAdjustment' (integer, positive if bad, negative if good). " +
                        "Message: " + text;

                Map<String, Object> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", prompt);

                Map<String, Object> body = new HashMap<>();
                body.put("model", "gpt-3.5-turbo");
                body.put("messages", List.of(message));
                body.put("temperature", 0.0);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                Map<?, ?> response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", request, Map.class);

                if (response != null && response.containsKey("choices")) {
                    List<?> choices = (List<?>) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                        Map<?, ?> msg = (Map<?, ?>) choice.get("message");
                        String content = (String) msg.get("content");

                        int sentScore = 0;
                        int frustAdj = 0;
                        String emotion = "Neutral";

                        if (content.contains("\"detectedEmotion\"")) {
                            emotion = content.split("\"detectedEmotion\"\\s*:\\s*\"")[1].split("\"")[0];
                        }
                        if (content.contains("\"sentimentScore\"")) {
                            sentScore = Integer.parseInt(content.split("\"sentimentScore\"\\s*:\\s*")[1].split(",")[0].replaceAll("[^\\d-]", ""));
                        }
                        if (content.contains("\"frustrationAdjustment\"")) {
                            frustAdj = Integer.parseInt(content.split("\"frustrationAdjustment\"\\s*:\\s*")[1].split("}")[0].replaceAll("[^\\d-]", ""));
                        }
                        return new SentimentResponse(emotion, sentScore, frustAdj);
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ OpenAI Sentiment API call failed, using built-in NLP rules fallback: {}", e.getMessage());
            }
        }

        // Built-in rule-based fallback
        String lowerText = text.toLowerCase();
        if (lowerText.contains("cancel") || lowerText.contains("unacceptable") || lowerText.contains("furious") || lowerText.contains("terrible") || lowerText.contains("disaster")) {
            return new SentimentResponse("High Frustration / At Risk", -85, 30);
        } else if (lowerText.contains("annoyed") || lowerText.contains("delay") || lowerText.contains("waiting") || lowerText.contains("late")) {
            return new SentimentResponse("Moderate Frustration", -40, 15);
        } else if (lowerText.contains("thank you") || lowerText.contains("resolved") || lowerText.contains("great") || lowerText.contains("appreciate")) {
            return new SentimentResponse("Satisfied", 75, -20);
        }
        return new SentimentResponse("Neutral", 0, 0);
    }
}
