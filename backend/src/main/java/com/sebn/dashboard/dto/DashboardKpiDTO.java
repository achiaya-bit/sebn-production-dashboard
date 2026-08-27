package com.sebn.dashboard.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardKpiDTO {

    private BigDecimal plannedQuantity;
    private BigDecimal reportedQuantity;
    private BigDecimal scrappedQuantity;
    private BigDecimal backlog;
    private BigDecimal completionRate;
}
