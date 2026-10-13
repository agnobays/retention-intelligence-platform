package com.retention.intelligence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "retention_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetentionPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "business_type", nullable = false)
    private String businessType; // e.g. FINANCIAL_SERVICES, LOGISTICS_COLD_CHAIN, FITNESS_GYM, ECOMMERCE

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_title", nullable = false)
    private String senderTitle;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "volume_decay_weight", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal volumeDecayWeight = new BigDecimal("0.50");

    @Column(name = "friction_weight", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal frictionWeight = new BigDecimal("0.60");

    @Column(name = "billing_risk_weight", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal billingRiskWeight = new BigDecimal("0.40");

    @Column(name = "auto_approval_threshold")
    @Builder.Default
    private Integer autoApprovalThreshold = 65;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
