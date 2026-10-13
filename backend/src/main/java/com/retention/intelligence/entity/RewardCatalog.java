package com.retention.intelligence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "reward_catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "reward_type", nullable = false)
    private String rewardType; // LOYALTY_POINTS | CASHBACK | VOUCHER | FEE_WAIVER | LIFESTYLE_EXPERIENCE | CUSTOM

    @Column(name = "reward_name", nullable = false)
    private String rewardName; // e.g. "UCount Rewards Points"

    @Column(name = "reward_value", nullable = false)
    private String rewardValue; // e.g. "5000 points", "R2,500 cashback"

    @Column(name = "target_segment", nullable = false)
    @Builder.Default
    private String targetSegment = "ALL"; // EVERYDAY_BANKING | PRIVATE_CLIENT | COMMERCIAL_SME | CORPORATE_CIB | ALL

    @Column(name = "min_frustration_score", nullable = false)
    @Builder.Default
    private Integer minFrustrationScore = 50;

    @Column(name = "requires_approval", nullable = false)
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;
}
