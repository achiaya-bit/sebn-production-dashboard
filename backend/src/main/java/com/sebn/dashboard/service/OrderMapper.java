package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.OrderDTO;
import com.sebn.dashboard.entity.WaoOrder;

/**
 * Maps WaoOrder entities to dashboard OrderDTOs.
 */
final class OrderMapper {

    private OrderMapper() {
    }

    static OrderDTO toDto(WaoOrder order) {
        var planned = order.getWaurmg();
        var reported = order.getWagfmg();

        return OrderDTO.builder()
                .orderNumber(order.getWaaunr())
                .partNumber(order.getWatenr())
                .plannedDate(order.getWaente())
                .plannedTime(order.getWaenjk())
                .endDate(order.getWastte())
                .endTime(order.getWastjk())
                .plannedQuantity(KpiCalculator.nullToZero(planned))
                .reportedQuantity(KpiCalculator.nullToZero(reported))
                .scrappedQuantity(KpiCalculator.nullToZero(order.getWaaumg()))
                .backlog(KpiCalculator.backlog(planned, reported))
                .status(order.getWastat())
                .progress(KpiCalculator.completionRate(planned, reported))
                .build();
    }
}
