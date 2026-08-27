import { useQuery } from "@tanstack/react-query";
import {
  getBacklogTrend,
  getDataFreshness,
  getKpis,
  getProductionTrend,
  getStatusCounts,
  getTopBacklog,
} from "@/services/dashboardService";
import { DEFAULT_ORDERS_PAGE_SIZE, getOrders } from "@/services/orderService";
import type { DashboardFilters } from "@/types/dashboard";

// Automatic background refresh interval for the Dashboard and Orders page.
// refetchIntervalInBackground: false means the timer pauses when the browser
// tab is hidden, preventing unnecessary requests while the user is away.
const REFETCH_INTERVAL_MS = 60_000;

function filtersKey(filters: DashboardFilters) {
  return [
    filters.startDate,
    filters.endDate,
    filters.status,
    filters.partNumber,
    filters.fromTime,
    filters.toTime,
  ] as const;
}

/**
 * Dashboard KPIs, charts and freshness — does NOT load /api/orders.
 *
 * All queries refresh automatically every 60 seconds while the tab is
 * visible. Existing data stays visible during background refetch; the
 * full loading skeleton is only shown on the very first load (isPending).
 */
export function useDashboardData(filters: DashboardFilters) {
  const key = filtersKey(filters);

  const sharedOptions = {
    refetchInterval: REFETCH_INTERVAL_MS,
    refetchIntervalInBackground: false,
  } as const;

  const kpisQuery = useQuery({
    queryKey: ["dashboard", "kpis", ...key],
    queryFn: () => getKpis(filters),
    ...sharedOptions,
  });

  const statusQuery = useQuery({
    queryKey: ["dashboard", "status", ...key],
    queryFn: () => getStatusCounts(filters),
    ...sharedOptions,
  });

  const backlogQuery = useQuery({
    queryKey: ["dashboard", "top-backlog", ...key],
    queryFn: () => getTopBacklog(filters),
    ...sharedOptions,
  });

  const productionTrendQuery = useQuery({
    queryKey: ["dashboard", "production-trend", ...key],
    queryFn: () => getProductionTrend(filters),
    ...sharedOptions,
  });

  const backlogTrendQuery = useQuery({
    queryKey: ["dashboard", "backlog-trend", ...key],
    queryFn: () => getBacklogTrend(filters),
    ...sharedOptions,
  });

  // Freshness is global (no filter dependency) and refreshes on the same schedule.
  const freshnessQuery = useQuery({
    queryKey: ["dashboard", "data-freshness"],
    queryFn: () => getDataFreshness(),
    ...sharedOptions,
  });

  const queries = [
    kpisQuery,
    statusQuery,
    backlogQuery,
    productionTrendQuery,
    backlogTrendQuery,
    freshnessQuery,
  ];
  // isPending = true only on the very first fetch (no cached data yet).
  // isFetching = true on every fetch including background refreshes.
  const isLoading = queries.some((q) => q.isPending);
  const isFetching = queries.some((q) => q.isFetching);
  const error = queries.find((q) => q.isError)?.error ?? null;

  const refetch = () => Promise.all(queries.map((q) => q.refetch()));

  return {
    kpis: kpisQuery.data,
    statusCounts: statusQuery.data ?? [],
    topBacklog: backlogQuery.data ?? [],
    productionTrend: productionTrendQuery.data ?? [],
    backlogTrend: backlogTrendQuery.data ?? [],
    freshness: freshnessQuery.data ?? null,
    isLoading,
    isFetching,
    error,
    refetch,
  };
}

/**
 * Production Orders page — loads one server page at a time.
 *
 * Background refresh every 60 seconds refreshes only the current page.
 * The user's current page and filters are preserved during automatic refresh.
 * Manual filter Apply resets to page 0 (handled in the route component).
 */
export function useOrdersData(
  filters: DashboardFilters,
  page = 0,
  size = DEFAULT_ORDERS_PAGE_SIZE,
) {
  const key = filtersKey(filters);

  const ordersQuery = useQuery({
    queryKey: ["orders", "page", page, size, ...key],
    queryFn: () => getOrders(filters, { page, size }),
    refetchInterval: REFETCH_INTERVAL_MS,
    refetchIntervalInBackground: false,
  });

  return {
    orders: ordersQuery.data?.content ?? [],
    page: ordersQuery.data?.page ?? page,
    size: ordersQuery.data?.size ?? size,
    totalElements: ordersQuery.data?.totalElements ?? 0,
    totalPages: ordersQuery.data?.totalPages ?? 0,
    isLoading: ordersQuery.isPending,
    isFetching: ordersQuery.isFetching,
    error: ordersQuery.error,
    refetch: () => ordersQuery.refetch(),
  };
}
