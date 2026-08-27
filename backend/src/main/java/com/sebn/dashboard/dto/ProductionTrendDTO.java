package com.sebn.dashboard.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

/**
 * Daily planned vs reported quantities grouped by planned start date ({@code WAENTE}).
 */
@Value
@Builder
public class ProductionTrendDTO {

    /** Date in {@code YYYYMMDD} format. */
    String date;

    BigDecimal plannedQuantity;

    BigDecimal reportedQuantity;
}
