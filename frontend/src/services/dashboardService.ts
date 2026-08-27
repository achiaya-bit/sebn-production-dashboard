import { fetchJson } from "@/services/api";
import {
  mapStatusCountFromDto,
  type BacklogTrendPoint,
  type DashboardFilters,
  type DashboardKpiDTO,
  type DashboardKpis,
  type DataFreshnessResponse,
  type OrderStatusCount,
  type PartBacklogDTO,
  type ProductionTrendPoint,
  type StatusDTO,
  type TopBacklogPart,
} from "@/types/dashboard";
import { API_BASE_URL, API_ERROR_MESSAGE, ApiError } from "@/services/api";

/** GET /api/dashboard/kpis */
export function getKpis(filters: DashboardFilters): Promise<DashboardKpis> {
  return fetchJson<DashboardKpiDTO>("/dashboard/kpis", filters);
}

/** GET /api/dashboard/status */
export function getStatusCounts(filters: DashboardFilters): Promise<OrderStatusCount[]> {
  return fetchJson<StatusDTO[]>("/dashboard/status", filters).then((data) =>
    data.map(mapStatusCountFromDto),
  );
}

/** GET /api/dashboard/top-backlog */
export function getTopBacklog(filters: DashboardFilters): Promise<TopBacklogPart[]> {
  return fetchJson<PartBacklogDTO[]>("/dashboard/top-backlog", filters);
}

/** GET /api/dashboard/production-trend */
export function getProductionTrend(filters: DashboardFilters): Promise<ProductionTrendPoint[]> {
  return fetchJson<ProductionTrendPoint[]>("/dashboard/production-trend", filters);
}

/** GET /api/dashboard/backlog-trend */
export function getBacklogTrend(filters: DashboardFilters): Promise<BacklogTrendPoint[]> {
  return fetchJson<BacklogTrendPoint[]>("/dashboard/backlog-trend", filters);
}

/**
 * GET /api/dashboard/data-freshness
 *
 * Returns dataset freshness metadata derived from WAO order fields (WARMDA,
 * WARMUZ, WAAEDA). All values come from the database — the server clock is
 * never used as a data timestamp.
 *
 * This endpoint takes no filter parameters — freshness is always global.
 */
export async function getDataFreshness(): Promise<DataFreshnessResponse> {
  const url = `${API_BASE_URL}/dashboard/data-freshness`;
  let response: Response;
  try {
    response = await fetch(url);
  } catch {
    throw new ApiError(API_ERROR_MESSAGE);
  }
  if (!response.ok) {
    throw new ApiError(API_ERROR_MESSAGE, response.status);
  }
  return response.json() as Promise<DataFreshnessResponse>;
}
