package com.retention.intelligence.mapper;

import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDTO toDto(Customer customer) {
        if (customer == null) return null;
        return CustomerDTO.builder()
                .id(customer.getId())
                .externalCustomerId(customer.getExternalCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .mrr(customer.getMrr())
                .arr(customer.getArr())
                .healthScore(customer.getHealthScore())
                .churnProbability(customer.getChurnProbability())
                .status(customer.getStatus())
                .contractRenewalDate(customer.getContractRenewalDate())
                .build();
    }
}
