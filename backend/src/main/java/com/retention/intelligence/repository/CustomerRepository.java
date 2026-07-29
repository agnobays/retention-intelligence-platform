package com.retention.intelligence.repository;

import com.retention.intelligence.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByCompanyId(UUID companyId);
    List<Customer> findByStatus(String status);
    Optional<Customer> findByCompanyIdAndExternalCustomerId(UUID companyId, String externalCustomerId);
}
