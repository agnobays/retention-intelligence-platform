package com.retention.intelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_value_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerValueScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private BigDecimal ltv;

    @Column(name = "usage_frequency_score")
    private Integer usageFrequencyScore;

    @Column(name = "support_ticket_volume")
    private Integer supportTicketVolume;

    @Column(name = "sla_tier")
    private String slaTier;

    @Column(name = "strategic_value_tier")
    private String strategicValueTier;

    @Column(name = "calculated_at", insertable = false, updatable = false)
    private ZonedDateTime calculatedAt;
}
