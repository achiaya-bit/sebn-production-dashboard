package com.sebn.dashboard.controller;

import com.sebn.dashboard.dto.BacklogTrendDTO;
import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.dto.DashboardKpiDTO;
import com.sebn.dashboard.dto.DataFreshnessDTO;
import com.sebn.dashboard.dto.PartBacklogDTO;
import com.sebn.dashboard.dto.ProductionTrendDTO;
import com.sebn.dashboard.dto.StatusDTO;
import com.sebn.dashboard.service.DashboardFilterFactory;
import com.sebn.dashboard.service.DashboardService;
import com.sebn.dashboard.service.DataFreshnessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dashboard", description = "Production dashboard KPIs, trends and aggregations")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DataFreshnessService dataFreshnessService;

    @GetMapping("/kpis")
    @Operation(summary = "Dashboard KPIs")
    public ResponseEntity<DashboardKpiDTO> getKpis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(dashboardService.getDashboardKPIs(toFilter(startDate, endDate, status, partNumber, fromTime, toTime)));
    }

    @GetMapping("/status")
    public ResponseEntity<List<StatusDTO>> getOrdersByStatus(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(dashboardService.getOrdersByStatus(toFilter(startDate, endDate, status, partNumber, fromTime, toTime)));
    }

    @GetMapping("/top-backlog")
    public ResponseEntity<List<PartBacklogDTO>> getTopBacklogParts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(
                dashboardService.getTop10BacklogParts(toFilter(startDate, endDate, status, partNumber, fromTime, toTime)));
    }

    @GetMapping("/backlog")
    public ResponseEntity<Map<String, BigDecimal>> getBacklog(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(
                Map.of("backlog", dashboardService.getBacklog(toFilter(startDate, endDate, status, partNumber, fromTime, toTime))));
    }

    @GetMapping("/completion-rate")
    public ResponseEntity<Map<String, BigDecimal>> getCompletionRate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(Map.of(
                "completionRate",
                dashboardService.getCompletionRate(toFilter(startDate, endDate, status, partNumber, fromTime, toTime))));
    }

    @GetMapping("/production-trend")
    @Operation(
            summary = "Planned vs reported over time",
            description = "Daily planned (SUM WAURMG) and reported (SUM WAGFMG) grouped by WAENTE")
    public ResponseEntity<List<ProductionTrendDTO>> getProductionTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(
                dashboardService.getProductionTrend(toFilter(startDate, endDate, status, partNumber, fromTime, toTime)));
    }

    @GetMapping("/backlog-trend")
    @Operation(
            summary = "Cumulative backlog trend",
            description = "Running backlog within the selected period, floored at zero")
    public ResponseEntity<List<BacklogTrendDTO>> getBacklogTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String partNumber,
            @RequestParam(required = false) String fromTime,
            @RequestParam(required = false) String toTime) {

        return ResponseEntity.ok(
                dashboardService.getBacklogTrend(toFilter(startDate, endDate, status, partNumber, fromTime, toTime)));
    }

    @GetMapping("/data-freshness")
    @Operation(
            summary = "Dataset freshness metadata",
            description = "Returns the latest reporting timestamp (WARMDA+WARMUZ) and " +
                          "order modification date (WAAEDA) from the database, plus the " +
                          "configured data mode. All values are derived from MySQL — the " +
                          "server clock is never used as a data timestamp.")
    public ResponseEntity<DataFreshnessDTO> getDataFreshness() {
        return ResponseEntity.ok(dataFreshnessService.getDataFreshness());
    }

    private DashboardFilter toFilter(
            String startDate,
            String endDate,
            String status,
            String partNumber,
            String fromTime,
            String toTime) {
        return DashboardFilterFactory.from(startDate, endDate, status, partNumber, fromTime, toTime);
    }
}
