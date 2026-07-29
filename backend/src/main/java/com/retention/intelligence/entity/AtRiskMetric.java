package com.retention.intelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "at_risk_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtRiskMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "metric_value")
    private String metricValue;

    @Column(name = "detected_at", insertable = false, updatable = false)
    private ZonedDateTime detectedAt;
}
