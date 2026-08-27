import { ApiError, API_BASE_URL, API_ERROR_MESSAGE, buildQueryParams } from "@/services/api";
import {
  mapPagedOrdersFromDto,
  type PagedOrders,
  type PagedOrdersDTO,
} from "@/types/order";
import type { DashboardFilters } from "@/types/dashboard";

export const DEFAULT_ORDERS_PAGE_SIZE = 25;

export interface OrdersPageParams {
  page?: number;
  size?: number;
}

/** GET /api/orders — server-side pagination (default page=0, size=25). */
export async function getOrders(
  filters: DashboardFilters,
  { page = 0, size = DEFAULT_ORDERS_PAGE_SIZE }: OrdersPageParams = {},
): Promise<PagedOrders> {
  const params = buildQueryParams(filters);
  params.set("page", String(page));
  params.set("size", String(size));

  const query = params.toString();
  const url = `${API_BASE_URL}/orders?${query}`;

  let response: Response;
  try {
    response = await fetch(url);
  } catch {
    throw new ApiError(API_ERROR_MESSAGE);
  }

  if (!response.ok) {
    throw new ApiError(API_ERROR_MESSAGE, response.status);
  }

  const data = (await response.json()) as PagedOrdersDTO;
  return mapPagedOrdersFromDto(data);
}
