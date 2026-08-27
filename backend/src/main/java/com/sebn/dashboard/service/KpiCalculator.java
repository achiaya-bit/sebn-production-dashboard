package com.sebn.dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Shared KPI calculation rules for the production dashboard.
 */
final class KpiCalculator {

    private static final int SCALE = 2;

    private KpiCalculator() {
    }

    static BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * Backlog = planned - reported, never negative.
     */
    static BigDecimal backlog(BigDecimal plannedQuantity, BigDecimal reportedQuantity) {
        BigDecimal backlog = nullToZero(plannedQuantity).subtract(nullToZero(reportedQuantity));
        return backlog.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : backlog;
    }

    /**
     * Ensures an aggregate backlog value is never negative.
     */
    static BigDecimal neverNegative(BigDecimal value) {
        BigDecimal safe = nullToZero(value);
        return safe.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : safe;
    }

    /**
     * Completion rate as percentage: (reported / planned) * 100, rounded to 2 decimals.
     * Returns 0 when planned quantity is zero.
     */
    static BigDecimal completionRate(BigDecimal plannedQuantity, BigDecimal reportedQuantity) {
        BigDecimal planned = nullToZero(plannedQuantity);
        if (planned.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SCALE);
        }
        return nullToZero(reportedQuantity)
                .multiply(BigDecimal.valueOf(100))
                .divide(planned, SCALE, RoundingMode.HALF_UP);
    }
}
