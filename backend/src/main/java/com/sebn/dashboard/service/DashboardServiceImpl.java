package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.BacklogTrendDTO;
import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.dto.DashboardKpiDTO;
import com.sebn.dashboard.dto.PartBacklogDTO;
import com.sebn.dashboard.dto.ProductionTrendDTO;
import com.sebn.dashboard.dto.StatusDTO;
import com.sebn.dashboard.repository.WaoOrderRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int TOP_BACKLOG_PARTS = 10;

    private final WaoOrderRepository waoOrderRepository;

    @Override
    public DashboardKpiDTO getDashboardKPIs(DashboardFilter filter) {
        DashboardFilter safeFilter = normalize(filter);
        BigDecimal plannedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumPlannedQuantity(safeFilter));
        BigDecimal reportedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumReportedQuantity(safeFilter));
        BigDecimal scrappedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumScrappedQuantity(safeFilter));

        return DashboardKpiDTO.builder()
                .plannedQuantity(plannedQuantity)
                .reportedQuantity(reportedQuantity)
                .scrappedQuantity(scrappedQuantity)
                .backlog(KpiCalculator.backlog(plannedQuantity, reportedQuantity))
                .completionRate(KpiCalculator.completionRate(plannedQuantity, reportedQuantity))
                .build();
    }

    @Override
    public List<StatusDTO> getOrdersByStatus(DashboardFilter filter) {
        return waoOrderRepository.countOrdersGroupedByWastat(normalize(filter)).stream()
                .map(projection -> StatusDTO.builder()
                        .status(projection.getStatus())
                        .totalOrders(projection.getOrderCount())
                        .build())
                .toList();
    }

    @Override
    public List<PartBacklogDTO> getTop10BacklogParts(DashboardFilter filter) {
        return waoOrderRepository
                .findTopPartNumbersByBacklog(normalize(filter), PageRequest.of(0, TOP_BACKLOG_PARTS))
                .stream()
                .map(projection -> PartBacklogDTO.builder()
                        .partNumber(projection.getPartNumber())
                        .backlog(KpiCalculator.neverNegative(projection.getBacklog()))
                        .build())
                .toList();
    }

    @Override
    public BigDecimal getBacklog(DashboardFilter filter) {
        DashboardFilter safeFilter = normalize(filter);
        BigDecimal plannedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumPlannedQuantity(safeFilter));
        BigDecimal reportedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumReportedQuantity(safeFilter));
        return KpiCalculator.backlog(plannedQuantity, reportedQuantity);
    }

    @Override
    public BigDecimal getCompletionRate(DashboardFilter filter) {
        DashboardFilter safeFilter = normalize(filter);
        BigDecimal plannedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumPlannedQuantity(safeFilter));
        BigDecimal reportedQuantity = KpiCalculator.nullToZero(waoOrderRepository.sumReportedQuantity(safeFilter));
        return KpiCalculator.completionRate(plannedQuantity, reportedQuantity);
    }

    @Override
    public List<ProductionTrendDTO> getProductionTrend(DashboardFilter filter) {
        return waoOrderRepository.findDailyProductionByPlannedDate(normalize(filter)).stream()
                .map(row -> ProductionTrendDTO.builder()
                        .date(row.getDate())
                        .plannedQuantity(KpiCalculator.nullToZero(row.getPlannedQuantity()))
                        .reportedQuantity(KpiCalculator.nullToZero(row.getReportedQuantity()))
                        .build())
                .toList();
    }

    @Override
    public List<BacklogTrendDTO> getBacklogTrend(DashboardFilter filter) {
        return TrendCalculator.cumulativeBacklogTrend(
                waoOrderRepository.findDailyProductionByPlannedDate(normalize(filter)));
    }

    private DashboardFilter normalize(DashboardFilter filter) {
        return filter != null ? filter : DashboardFilter.empty();
    }
}
