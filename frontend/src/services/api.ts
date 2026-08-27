import { toApiDate, toApiStatus } from "@/lib/statusMapping";
import type { DashboardFilters } from "@/types/dashboard";

export const API_BASE_URL =
  import.meta.env["VITE_API_BASE_URL"] ?? "http://localhost:8080/api";

export const API_ERROR_MESSAGE =
  "Unable to connect to the production dashboard API.";

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status?: number,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export function buildQueryParams(filters: DashboardFilters): URLSearchParams {
  const params = new URLSearchParams();

  const startDate = toApiDate(filters.startDate);
  const endDate = toApiDate(filters.endDate);
  const status = toApiStatus(filters.status);
  const partNumber = filters.partNumber.trim();

  if (startDate) params.set("startDate", startDate);
  if (endDate) params.set("endDate", endDate);
  if (status) params.set("status", status);
  if (partNumber) params.set("partNumber", partNumber);

  // Send fromTime and toTime only when both are present (backend requires both together).
  const from = filters.fromTime?.trim() ?? "";
  const to = filters.toTime?.trim() ?? "";
  if (from && to) {
    params.set("fromTime", from);
    params.set("toTime", to);
  }

  return params;
}

export async function fetchJson<T>(
  path: string,
  filters: DashboardFilters,
): Promise<T> {
  const params = buildQueryParams(filters);
  const query = params.toString();
  const url = `${API_BASE_URL}${path}${query ? `?${query}` : ""}`;

  let response: Response;
  try {
    response = await fetch(url);
  } catch {
    throw new ApiError(API_ERROR_MESSAGE);
  }

  if (!response.ok) {
    throw new ApiError(API_ERROR_MESSAGE, response.status);
  }

  return response.json() as Promise<T>;
}
