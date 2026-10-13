package com.retention.intelligence.service;

import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.entity.Lead;
import com.retention.intelligence.repository.CustomerRepository;
import com.retention.intelligence.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    @Transactional
    public Lead createLead(Lead lead) {
        return leadRepository.save(lead);
    }

    @Transactional
    public Customer convertLead(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        // Retrieve any existing customer to grab the company
        // (Assuming a single tenant for this MVP)
        Customer existingCustomer = customerRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No base customer found to copy company config"));

        Customer newCustomer = new Customer();
        newCustomer.setExternalCustomerId("LD-CONV-" + System.currentTimeMillis() % 100000);
        newCustomer.setName(lead.getName());
        newCustomer.setEmail(lead.getName().replaceAll("\\s+", "").toLowerCase() + "@converted.com");
        newCustomer.setCustomerSegment(lead.getIndustrySegment());
        newCustomer.setArr(lead.getEstimatedArr());
        newCustomer.setMrr(lead.getEstimatedArr().divide(new java.math.BigDecimal("12"), java.math.RoundingMode.HALF_UP));
        newCustomer.setHealthScore(100);
        newCustomer.setFrustrationScore(0);
        newCustomer.setChurnProbability(new java.math.BigDecimal("0.00"));
        newCustomer.setStatus("ACTIVE");
        newCustomer.setCompany(existingCustomer.getCompany());

        customerRepository.save(newCustomer);
        
        lead.setStatus("CONVERTED");
        leadRepository.save(lead);

        return newCustomer;
    }
}
