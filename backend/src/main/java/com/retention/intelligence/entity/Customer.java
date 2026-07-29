package com.retention.intelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "external_customer_id", nullable = false)
    private String externalCustomerId;

    @Column(nullable = false)
    private String name;

    private String email;

    private BigDecimal mrr;
    private BigDecimal arr;

    @Column(name = "health_score")
    private Integer healthScore;

    @Column(name = "churn_probability")
    private BigDecimal churnProbability;

    private String status; // ACTIVE, AT_RISK, RECOVERING, CHURNED, SAVED

    @Column(name = "contract_renewal_date")
    private LocalDate contractRenewalDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;
}
