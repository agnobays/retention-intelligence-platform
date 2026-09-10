package com.retention.intelligence.service;

import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.RecoveryPlan;
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
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api-key:re_demo_key}")
    private String resendApiKey;

    @Value("${resend.from-email:retention@agnoandfriends.com}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendRecoveryEmail(RecoveryPlan plan) {
        Customer customer = plan.getCustomer();
        String customerName = customer != null ? customer.getName() : "Valued Corporate Client";
        String externalId = customer != null ? customer.getExternalCustomerId() : "SB-CIB-1001";
        int discount = plan.getDiscountPercentage() != null ? plan.getDiscountPercentage() : 15;

        String toEmail;
        String subject;
        String htmlContent;

        if (customerName.toLowerCase().contains("shoprite") || discount >= 15) {
            toEmail = "zolani1999@gmail.com";
            subject = "Standard Bank CIB: Executive Fee Concession & Dedicated RM Outreach for " + customerName;
            htmlContent = buildTier1EmailTemplate(customerName, externalId, discount, plan.getRecommendedAction());
        } else {
            toEmail = "vgnobookings@gmail.com";
            subject = "Standard Bank CIB: Custom FX Rate Lock & Concession Offer for " + customerName;
            htmlContent = buildTier2EmailTemplate(customerName, externalId, discount, plan.getRecommendedAction());
        }

        dispatchResendEmail(toEmail, subject, htmlContent);
    }

    private void dispatchResendEmail(String toEmail, String subject, String htmlContent) {
        log.info("Preparing Resend Email dispatch from {} to recipient {}", fromEmail, toEmail);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", "Standard Bank CIB <" + fromEmail + ">");
            body.put("to", List.of(toEmail));
            body.put("subject", subject);
            body.put("html", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            if (resendApiKey != null && resendApiKey.startsWith("re_") && !resendApiKey.equals("re_demo_key")) {
                restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
                log.info("Resend Email successfully sent to {}", toEmail);
            } else {
                log.warn("Resend API key unconfigured or using demo key. Simulated Email dispatch to: {}", toEmail);
            }
        } catch (Exception e) {
            log.error("Failed to send Resend API email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildTier1EmailTemplate(String customerName, String externalId, int discount, String action) {
        return "<html><body style='font-family: Arial, sans-serif; background-color: #0f172a; color: #f8fafc; padding: 24px;'>" +
               "<div style='max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 32px; border: 1px solid #334155;'>" +
               "<h2 style='color: #60a5fa; margin-top: 0;'>Standard Bank Corporate & Investment Banking</h2>" +
               "<hr style='border-color: #334155;'/>" +
               "<h3>Executive Retention Concession Notice</h3>" +
               "<p>Dear Treasury & Corporate Finance Team at <strong>" + customerName + "</strong> (" + externalId + "),</p>" +
               "<p>Following our automated risk intelligence review, Standard Bank CIB has authorized a dedicated corporate retention strategy:</p>" +
               "<div style='background: #0f172a; padding: 16px; border-radius: 8px; border-left: 4px solid #3b82f6; margin: 16px 0;'>" +
               "<p style='margin: 0; font-size: 14px;'><strong>Approved Concession:</strong> " + action + "</p>" +
               "<p style='margin: 8px 0 0 0; font-size: 14px; color: #34d399;'><strong>Corporate Fee Discount:</strong> " + discount + "%</p>" +
               "</div>" +
               "<p>A Senior CIB Relationship Manager (Sipho Dlamini) has been assigned to coordinate your merchant clearing and transactional facilities.</p>" +
               "<p style='color: #94a3b8; font-size: 12px; margin-top: 24px;'>Confidential Notice - Standard Bank South Africa CIB Platform</p>" +
               "</div></body></html>";
    }

    private String buildTier2EmailTemplate(String customerName, String externalId, int discount, String action) {
        return "<html><body style='font-family: Arial, sans-serif; background-color: #0f172a; color: #f8fafc; padding: 24px;'>" +
               "<div style='max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 32px; border: 1px solid #334155;'>" +
               "<h2 style='color: #60a5fa; margin-top: 0;'>Standard Bank Corporate & Investment Banking</h2>" +
               "<hr style='border-color: #334155;'/>" +
               "<h3>Treasury FX Rate Concession & Restructure Offer</h3>" +
               "<p>Dear Treasury Team at <strong>" + customerName + "</strong> (" + externalId + "),</p>" +
               "<p>Standard Bank CIB has approved a customized Foreign Exchange (FX) rate lock and credit facility adjustment:</p>" +
               "<div style='background: #0f172a; padding: 16px; border-radius: 8px; border-left: 4px solid #8b5cf6; margin: 16px 0;'>" +
               "<p style='margin: 0; font-size: 14px;'><strong>Action:</strong> " + action + "</p>" +
               "<p style='margin: 8px 0 0 0; font-size: 14px; color: #34d399;'><strong>FX Concession Discount:</strong> " + discount + "%</p>" +
               "</div>" +
               "<p>Please contact your CIB desk or reply to this communication to activate your rate lock.</p>" +
               "<p style='color: #94a3b8; font-size: 12px; margin-top: 24px;'>Confidential Notice - Standard Bank South Africa CIB Platform</p>" +
               "</div></body></html>";
    }
}
