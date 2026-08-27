package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.BacklogTrendDTO;
import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.dto.DashboardKpiDTO;
import com.sebn.dashboard.dto.PartBacklogDTO;
import com.sebn.dashboard.dto.ProductionTrendDTO;
import com.sebn.dashboard.dto.StatusDTO;
import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

    DashboardKpiDTO getDashboardKPIs(DashboardFilter filter);

    List<StatusDTO> getOrdersByStatus(DashboardFilter filter);

    List<PartBacklogDTO> getTop10BacklogParts(DashboardFilter filter);

    BigDecimal getBacklog(DashboardFilter filter);

    BigDecimal getCompletionRate(DashboardFilter filter);

    List<ProductionTrendDTO> getProductionTrend(DashboardFilter filter);

    List<BacklogTrendDTO> getBacklogTrend(DashboardFilter filter);
}
