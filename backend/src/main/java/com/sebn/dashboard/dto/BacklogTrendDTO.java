package com.sebn.dashboard.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

/**
 * Daily backlog evolution within the selected period.
 * {@code cumulativeBacklog} is the running total of daily differences, floored at zero.
 */
@Value
@Builder
public class BacklogTrendDTO {

    /** Date in {@code YYYYMMDD} format. */
    String date;

    BigDecimal plannedQuantity;

    BigDecimal reportedQuantity;

    /** {@code plannedQuantity - reportedQuantity} for the day. */
    BigDecimal dailyDifference;

    /**
     * Running backlog within the selected period:
     * {@code MAX(0, cumulativeBacklog(t-1) + dailyDifference(t))}, starting at 0.
     */
    BigDecimal cumulativeBacklog;
}
