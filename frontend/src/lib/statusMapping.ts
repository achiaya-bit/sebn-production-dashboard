import type { OrderStatus } from "@/types/order";

/** Human-readable labels shown in the UI (never raw backend codes). */
export const STATUS_LABELS: Record<OrderStatus, string> = {
  COMPLETED: "Completed",
  IN_PROGRESS: "In Progress",
  TRANSFERRED: "Transferred",
};

/** Backend status code → UI status */
const API_STATUS_TO_UI: Record<string, OrderStatus> = {
  "50": "COMPLETED",
  "30": "IN_PROGRESS",
  "15": "TRANSFERRED",
};

/** UI status → backend status code */
const UI_STATUS_TO_API: Record<OrderStatus, string> = {
  COMPLETED: "50",
  IN_PROGRESS: "30",
  TRANSFERRED: "15",
};

export function toApiDate(date: string): string | undefined {
  const trimmed = date.trim();
  if (!trimmed) return undefined;
  return trimmed.replace(/-/g, "");
}

export function fromApiDate(date: string): string {
  if (/^\d{8}$/.test(date)) {
    return `${date.slice(0, 4)}-${date.slice(4, 6)}-${date.slice(6, 8)}`;
  }
  return date;
}

export function toApiStatus(status: OrderStatus | "ALL"): string | undefined {
  if (status === "ALL") return undefined;
  return UI_STATUS_TO_API[status];
}

export function fromApiStatus(status: string): OrderStatus {
  return API_STATUS_TO_UI[status] ?? "TRANSFERRED";
}

export function getStatusLabel(status: OrderStatus): string {
  return STATUS_LABELS[status];
}
