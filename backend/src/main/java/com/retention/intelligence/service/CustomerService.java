package com.retention.intelligence.service;

import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<CustomerDTO> getCustomersByCompany(UUID companyId) {
        return customerRepository.findByCompanyId(companyId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO importCustomer(CustomerDTO dto) {
        // Skeleton logic for customer import
        Customer customer = Customer.builder()
                .externalCustomerId(dto.getExternalCustomerId())
                .name(dto.getName())
                .email(dto.getEmail())
                .mrr(dto.getMrr())
                .arr(dto.getArr())
                .healthScore(100)
                .status("ACTIVE")
                .build();
        Customer saved = customerRepository.save(customer);
        return mapToDTO(saved);
    }

    private CustomerDTO mapToDTO(Customer customer) {
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
                .build();
    }
}
