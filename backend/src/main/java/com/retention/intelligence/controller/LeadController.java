package com.retention.intelligence.controller;

import com.retention.intelligence.entity.Lead;
import com.retention.intelligence.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER', 'ANALYST')")
    public ResponseEntity<List<Lead>> getLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Lead> createLead(@jakarta.validation.Valid @RequestBody com.retention.intelligence.dto.LeadDTO leadDTO) {
        Lead lead = new Lead();
        lead.setName(leadDTO.getName());
        lead.setIndustrySegment(leadDTO.getIndustrySegment());
        lead.setPropensityToBuy(leadDTO.getPropensityToBuy());
        lead.setEstimatedArr(leadDTO.getEstimatedArr());
        lead.setStatus(leadDTO.getStatus());
        
        return ResponseEntity.ok(leadService.createLead(lead));
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Map<String, String>> convertLead(@PathVariable UUID id) {
        leadService.convertLead(id);
        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "message", "Lead successfully converted to Customer"
        ));
    }
}
