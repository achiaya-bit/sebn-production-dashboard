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
public class OrderDTO {

    private String orderNumber;
    private String partNumber;
    private String plannedDate;
    private String plannedTime;
    private String endDate;
    private String endTime;
    private BigDecimal plannedQuantity;
    private BigDecimal reportedQuantity;
    private BigDecimal scrappedQuantity;
    private BigDecimal backlog;
    private String status;
    private BigDecimal progress;
}
