import { fromApiStatus } from "@/lib/statusMapping";
import type { OrderStatus } from "@/types/order";

/** Response item from GET /api/dashboard/production-trend */
export interface ProductionTrendPoint {
  date: string;
  plannedQuantity: number;
  reportedQuantity: number;
}

/** Response item from GET /api/dashboard/backlog-trend */
export interface BacklogTrendPoint {
  date: string;
  plannedQuantity: number;
  reportedQuantity: number;
  dailyDifference: number;
  cumulativeBacklog: number;
}

/**
 * Response from GET /api/dashboard/data-freshness.
 *
 * latestReportedAt   – ISO-8601 local datetime built from MAX(WARMDA+WARMUZ) in the
 *                      database. Reflects the newest reporting entry in the imported
 *                      dataset. Does NOT mean the application is connected live to WAO.
 * latestOrderModificationDate – MAX(WAAEDA) in YYYYMMDD format, or null.
 * dataMode           – "IMPORTED" unless the backend is explicitly configured with
 *                      DATA_MODE=LIVE after a real WAO sync process is in place.
 */
export interface DataFreshnessResponse {
  latestReportedAt: string | null;
  latestOrderModificationDate: string | null;
  dataMode: "IMPORTED" | "LIVE" | string;
}

/** Response from GET /api/dashboard/kpis */
export interface DashboardKpiDTO {
  plannedQuantity: number;
  reportedQuantity: number;
  scrappedQuantity: number;
  backlog: number;
  completionRate: number;
}

export type DashboardKpis = DashboardKpiDTO;

/** Response item from GET /api/dashboard/status */
export interface StatusDTO {
  status: string;
  totalOrders: number;
}

export interface OrderStatusCount {
  status: OrderStatus;
  totalOrders: number;
}

/** Response item from GET /api/dashboard/top-backlog */
export interface PartBacklogDTO {
  partNumber: string;
  backlog: number;
}

export type TopBacklogPart = PartBacklogDTO;

export interface DashboardFilters {
  startDate: string;
  endDate: string;
  fromTime: string;
  toTime: string;
  status: OrderStatus | "ALL";
  partNumber: string;
}

export const defaultFilters: DashboardFilters = {
  startDate: "",
  endDate: "",
  fromTime: "",
  toTime: "",
  status: "ALL",
  partNumber: "",
};

export function mapStatusCountFromDto(dto: StatusDTO): OrderStatusCount {
  return {
    status: fromApiStatus(dto.status),
    totalOrders: dto.totalOrders,
  };
}
