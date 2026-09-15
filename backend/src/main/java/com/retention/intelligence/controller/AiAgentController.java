package com.retention.intelligence.controller;

import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.service.AiAgentService;
import com.retention.intelligence.service.EmailService;
import com.retention.intelligence.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/ai", "/ai"})
@RequiredArgsConstructor
@Tag(name = "Autonomous AI Retention Agent", description = "Endpoints for dynamic issue-tailored email synthesis and automated follow-up ticket tracking")
public class AiAgentController {

    private final AiAgentService aiAgentService;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @PostMapping("/generate-email/{customerId}")
    @Operation(summary = "Generate Custom AI Email Tailored to Customer Issue", description = "Analyzes customer specific issue, frustration telemetry, and generates custom tailored email with concession voucher")
    public ResponseEntity<AiAgentService.CustomEmailResponse> generateCustomEmail(@PathVariable UUID customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        AiAgentService.CustomEmailResponse emailResp = aiAgentService.generateTailoredCustomerEmail(customer);

        if (customer != null && auditLogService != null) {
            auditLogService.recordLog(
                "SESS-" + System.currentTimeMillis() % 1000000,
                customer.getExternalCustomerId(),
                customer.getName(),
                "AI_EMAIL_SYNTHESIZED",
                "Autonomous AI Executive Desk",
                customer.getEmail(),
                emailResp.getSubject(),
                emailResp.getTextSummary(),
                customer.getRewardValue()
            );
        }

        if (customer != null) {
            notificationService.broadcastNotification(
                "🤖 AI Agent Generated Email",
                "Custom retention email composed for " + customer.getName() + " addressing issue: " + customer.getIssueCategory(),
                "email"
            );
        }

        return ResponseEntity.ok(emailResp);
    }

    @PostMapping("/send-custom-email/{customerId}")
    @Operation(summary = "Dispatch Custom AI Email via Resend API", description = "Dispatches the AI generated custom email to customer inbox and triggers follow-up tracking")
    public ResponseEntity<Map<String, String>> sendCustomEmail(@PathVariable UUID customerId, @RequestBody Map<String, String> payload) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        String recipient = payload.get("recipientEmail");
        String subject = payload.get("subject");
        String htmlBody = payload.get("htmlContent");

        String result = emailService.sendDirectEmail(
            customer != null ? customer.getName() : "Valued Client",
            customer != null ? customer.getExternalCustomerId() : "SB-CIB-1001",
            15,
            customer != null && customer.getRewardValue() != null ? customer.getRewardValue() : "Concession Voucher",
            recipient
        );

        if (customer != null && auditLogService != null) {
            auditLogService.recordLog(
                "SESS-" + System.currentTimeMillis() % 1000000,
                customer.getExternalCustomerId(),
                customer.getName(),
                "CUSTOM_EMAIL_DISPATCHED",
                "Sipho Dlamini (Senior Relationship Manager)",
                recipient != null ? recipient : customer.getEmail(),
                subject != null ? subject : "Standard Bank Customer Resolution Notice",
                htmlBody != null ? htmlBody : "Custom Care Message Dispatched",
                customer.getRewardValue()
            );
        }

        if (customer != null) {
            notificationService.broadcastNotification(
                "📧 Custom AI Email Dispatched",
                "Email sent to " + (recipient != null ? recipient : customer.getEmail()) + " for " + customer.getName() + ". Follow-up tracker active.",
                "success"
            );
        }

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "dispatchResult", result,
            "message", "Custom issue-tailored email successfully dispatched."
        ));
    }

    @PostMapping("/follow-up/{customerId}")
    @Operation(summary = "Trigger Autonomous AI Ticket Follow-Up", description = "Executes automated follow-up check-in for open customer tickets until case is SAVED")
    public ResponseEntity<AiAgentService.CustomEmailResponse> triggerFollowUp(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "1") int followUpNumber) {
        
        Customer customer = customerRepository.findById(customerId).orElse(null);
        AiAgentService.CustomEmailResponse followUpResp = aiAgentService.generateFollowUpCheckIn(customer, followUpNumber);

        if (customer != null) {
            notificationService.broadcastNotification(
                "🔄 Autonomous Ticket Follow-up #" + followUpNumber,
                "Follow-up check-in sent to " + customer.getName() + " regarding open ticket: " + customer.getIssueCategory(),
                "workflow"
            );
        }

        return ResponseEntity.ok(followUpResp);
    }
}
