package com.retention.intelligence.service;

import com.retention.intelligence.dto.CustomerDTO;
import com.retention.intelligence.entity.Customer;
import com.retention.intelligence.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(UUID.randomUUID())
                .externalCustomerId("CUST-1001")
                .name("Acme Corp")
                .email("contact@acme.com")
                .mrr(new BigDecimal("5000.00"))
                .arr(new BigDecimal("60000.00"))
                .healthScore(85)
                .status("ACTIVE")
                .build();
    }

    @Test
    void testGetCustomersByCompany() {
        UUID companyId = UUID.randomUUID();
        when(customerRepository.findByCompanyId(companyId)).thenReturn(List.of(customer));

        List<CustomerDTO> result = customerService.getCustomersByCompany(companyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Acme Corp", result.get(0).getName());
    }

    @Test
    void testImportCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO dto = CustomerDTO.builder()
                .externalCustomerId("CUST-1001")
                .name("Acme Corp")
                .email("contact@acme.com")
                .build();

        CustomerDTO saved = customerService.importCustomer(dto);

        assertNotNull(saved);
        assertEquals("Acme Corp", saved.getName());
    }
}
