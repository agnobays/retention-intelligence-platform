package com.retention.intelligence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadDTO {

    @NotBlank(message = "Lead name is required")
    private String name;

    @NotBlank(message = "Industry segment is required")
    private String industrySegment;

    @NotNull(message = "Propensity to buy is required")
    @Min(value = 0, message = "Propensity to buy cannot be less than 0")
    @Max(value = 100, message = "Propensity to buy cannot be greater than 100")
    private Integer propensityToBuy;

    @NotNull(message = "Estimated ARR is required")
    @Min(value = 0, message = "Estimated ARR cannot be negative")
    private BigDecimal estimatedArr;

    @NotBlank(message = "Status is required")
    private String status;
}
