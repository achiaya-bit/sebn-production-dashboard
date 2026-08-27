package com.sebn.dashboard.repository;

import com.sebn.dashboard.dto.DashboardFilter;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Custom aggregate queries supporting dynamic {@link DashboardFilter} criteria.
 */
public interface WaoOrderRepositoryCustom {

    BigDecimal sumPlannedQuantity(DashboardFilter filter);

    BigDecimal sumReportedQuantity(DashboardFilter filter);

    BigDecimal sumScrappedQuantity(DashboardFilter filter);

    List<WaoOrderRepository.StatusCountProjection> countOrdersGroupedByWastat(DashboardFilter filter);

    List<WaoOrderRepository.PartBacklogProjection> findTopPartNumbersByBacklog(
            DashboardFilter filter,
            Pageable pageable);

    List<WaoOrderRepository.DailyProductionProjection> findDailyProductionByPlannedDate(DashboardFilter filter);
}
