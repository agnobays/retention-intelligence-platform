package com.retention.intelligence.repository;

import com.retention.intelligence.entity.CustomerValueScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerValueScoreRepository extends JpaRepository<CustomerValueScore, UUID> {
    Optional<CustomerValueScore> findByCustomerId(UUID customerId);
}
