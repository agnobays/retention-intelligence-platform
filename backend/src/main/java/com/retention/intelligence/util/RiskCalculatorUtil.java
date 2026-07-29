package com.retention.intelligence.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RiskCalculatorUtil {

    public static BigDecimal calculateChurnRisk(int healthScore, int supportTickets, int usageScore) {
        // Formula: Risk % = 100 - (0.5 * healthScore + 0.3 * usageScore - 2.0 * supportTickets)
        double score = 100.0 - (0.5 * healthScore + 0.3 * usageScore - 2.0 * supportTickets);
        double clamped = Math.max(0.0, Math.min(100.0, score));
        return BigDecimal.valueOf(clamped).setScale(2, RoundingMode.HALF_UP);
    }
}
