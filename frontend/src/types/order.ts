import { fromApiDate, fromApiStatus } from "@/lib/statusMapping";

export type OrderStatus = "COMPLETED" | "IN_PROGRESS" | "TRANSFERRED";

/** Response item from GET /api/orders */
export interface OrderDTO {
  orderNumber: string;
  partNumber: string;
  plannedDate: string;
  plannedQuantity: number;
  reportedQuantity: number;
  scrappedQuantity: number;
  backlog: number;
  status: string;
  progress: number;
}

export interface ProductionOrder {
  orderNumber: string;
  partNumber: string;
  plannedDate: string;
  plannedQuantity: number;
  reportedQuantity: number;
  scrappedQuantity: number;
  backlog: number;
  status: OrderStatus;
  progress: number;
}

/** Paginated response from GET /api/orders */
export interface PagedOrdersDTO {
  content: OrderDTO[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PagedOrders {
  content: ProductionOrder[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export function mapOrderFromDto(dto: OrderDTO): ProductionOrder {
  return {
    orderNumber: dto.orderNumber,
    partNumber: dto.partNumber,
    plannedDate: fromApiDate(dto.plannedDate),
    plannedQuantity: dto.plannedQuantity,
    reportedQuantity: dto.reportedQuantity,
    scrappedQuantity: dto.scrappedQuantity,
    backlog: dto.backlog,
    status: fromApiStatus(dto.status),
    progress: dto.progress,
  };
}

export function mapPagedOrdersFromDto(dto: PagedOrdersDTO): PagedOrders {
  return {
    content: dto.content.map(mapOrderFromDto),
    page: dto.page,
    size: dto.size,
    totalElements: dto.totalElements,
    totalPages: dto.totalPages,
  };
}
