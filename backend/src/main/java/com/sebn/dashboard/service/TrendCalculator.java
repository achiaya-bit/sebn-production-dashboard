package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.BacklogTrendDTO;
import com.sebn.dashboard.repository.WaoOrderRepository.DailyProductionProjection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes cumulative backlog trend from daily production aggregates.
 */
final class TrendCalculator {

    private TrendCalculator() {
    }

    static List<BacklogTrendDTO> cumulativeBacklogTrend(List<DailyProductionProjection> dailyRows) {
        List<BacklogTrendDTO> result = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;

        for (DailyProductionProjection row : dailyRows) {
            BigDecimal planned = KpiCalculator.nullToZero(row.getPlannedQuantity());
            BigDecimal reported = KpiCalculator.nullToZero(row.getReportedQuantity());
            BigDecimal dailyDifference = planned.subtract(reported);
            cumulative = cumulative.add(dailyDifference);
            if (cumulative.compareTo(BigDecimal.ZERO) < 0) {
                cumulative = BigDecimal.ZERO;
            }

            result.add(BacklogTrendDTO.builder()
                    .date(row.getDate())
                    .plannedQuantity(planned)
                    .reportedQuantity(reported)
                    .dailyDifference(dailyDifference)
                    .cumulativeBacklog(cumulative)
                    .build());
        }

        return result;
    }
}
