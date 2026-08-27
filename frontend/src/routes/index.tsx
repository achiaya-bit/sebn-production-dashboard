import { useEffect, useRef, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { AlertTriangle, ClipboardList, Gauge, PackageCheck, Trash2 } from "lucide-react";
import { DashboardShell } from "@/components/dashboard/DashboardShell";
import { DashboardFilters } from "@/components/dashboard/DashboardFilters";
import { FreshnessBar } from "@/components/dashboard/FreshnessBar";
import {
  ChartsSkeleton,
  DashboardApiError,
  KpiCardsSkeleton,
} from "@/components/dashboard/DashboardDataState";
import { KpiCard } from "@/components/dashboard/KpiCard";
import { BacklogChart } from "@/components/dashboard/BacklogChart";
import { StatusChart } from "@/components/dashboard/StatusChart";
import { PlannedVsReportedChart } from "@/components/dashboard/PlannedVsReportedChart";
import { CumulativeBacklogChart } from "@/components/dashboard/CumulativeBacklogChart";
import { useDashboardData } from "@/hooks/useDashboardData";
import { formatNumber, formatPercent } from "@/lib/format";
import { API_ERROR_MESSAGE } from "@/services/api";
import { defaultFilters, type DashboardFilters as Filters } from "@/types/dashboard";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Production Overview | SEBN Production Dashboard" },
      {
        name: "description",
        content:
          "Monitor planned production, reported quantities, scrap and backlog for the SEBN welding production area.",
      },
      { property: "og:title", content: "Production Overview | SEBN Production Dashboard" },
      {
        property: "og:description",
        content: "Production monitoring: planned vs reported quantities and backlog.",
      },
    ],
  }),
  component: DashboardPage,
});

function DashboardPage() {
  const [filters, setFilters] = useState<Filters>(defaultFilters);
  const {
    kpis,
    statusCounts,
    topBacklog,
    productionTrend,
    backlogTrend,
    freshness,
    isLoading,
    isFetching,
    error,
    refetch,
  } = useDashboardData(filters);

  // Track when the last successful fetch completed so FreshnessBar can display
  // "Dashboard refreshed: <time>" — this is the client fetch time, NOT the
  // dataset timestamp. The dataset timestamp comes from the freshness endpoint.
  const [lastRefreshedAt, setLastRefreshedAt] = useState<string | null>(null);
  const wasFetchingRef = useRef(false);
  useEffect(() => {
    if (wasFetchingRef.current && !isFetching && !error) {
      setLastRefreshedAt(new Date().toISOString());
    }
    wasFetchingRef.current = isFetching;
  }, [isFetching, error]);

  const errorMessage = error instanceof Error ? error.message : API_ERROR_MESSAGE;

  return (
    <DashboardShell
      title="Production Overview"
      subtitle="Monitor planned production, reported quantities and backlog in real time."
    >
      <DashboardFilters value={filters} onApply={setFilters} />

      {/* Freshness bar: dataset source, latest data timestamp, refresh status */}
      <FreshnessBar
        freshness={freshness}
        lastRefreshedAt={lastRefreshedAt}
        isFetching={isFetching}
        onRefresh={() => void refetch()}
      />

      {error && (
        <DashboardApiError
          message={errorMessage}
          onRetry={() => void refetch()}
          isRetrying={isFetching}
        />
      )}

      {isLoading ? (
        <>
          <KpiCardsSkeleton />
          <div className="grid gap-4 xl:grid-cols-2">
            <ChartsSkeleton />
            <ChartsSkeleton />
          </div>
          <div className="grid gap-4 xl:grid-cols-2">
            <ChartsSkeleton />
            <ChartsSkeleton />
          </div>
        </>
      ) : kpis ? (
        <>
          {/* ── KPI cards ────────────────────────────────────────── */}
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
            <KpiCard
              label="Planned Quantity"
              value={formatNumber(kpis.plannedQuantity)}
              hint="Total production planned"
              icon={ClipboardList}
              tone="primary"
            />
            <KpiCard
              label="Reported Quantity"
              value={formatNumber(kpis.reportedQuantity)}
              hint="Production reported"
              icon={PackageCheck}
              tone="success"
            />
            <KpiCard
              label="Scrapped Quantity"
              value={formatNumber(kpis.scrappedQuantity)}
              hint="Rejected quantity"
              icon={Trash2}
              tone="warning"
            />
            <KpiCard
              label="Backlog"
              value={formatNumber(kpis.backlog)}
              hint="Remaining quantity to produce"
              icon={AlertTriangle}
              tone="critical"
              emphasis
            />
            <KpiCard
              label="Completion Rate"
              value={formatPercent(kpis.completionRate)}
              hint="Production completion"
              icon={Gauge}
              tone="primary"
              progress={kpis.completionRate}
            />
          </div>

          {/* ── Primary charts ─────────────────────────────────────── */}
          <div className="grid gap-4 xl:grid-cols-2">
            <PlannedVsReportedChart
              data={productionTrend}
              isLoading={isFetching && productionTrend.length === 0}
            />
            <CumulativeBacklogChart
              data={backlogTrend}
              isLoading={isFetching && backlogTrend.length === 0}
            />
          </div>

          {/* ── Secondary charts ─────────────────────────────────── */}
          <div className="grid gap-4 xl:grid-cols-2">
            <BacklogChart data={topBacklog} />
            <StatusChart data={statusCounts} />
          </div>
        </>
      ) : null}
    </DashboardShell>
  );
}
