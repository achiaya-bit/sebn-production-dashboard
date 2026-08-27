import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { DashboardShell } from "@/components/dashboard/DashboardShell";
import { DashboardFilters } from "@/components/dashboard/DashboardFilters";
import {
  DashboardApiError,
  OrdersTableSkeleton,
} from "@/components/dashboard/DashboardDataState";
import { OrdersTable } from "@/components/dashboard/OrdersTable";
import { useOrdersData } from "@/hooks/useDashboardData";
import { API_ERROR_MESSAGE } from "@/services/api";
import { DEFAULT_ORDERS_PAGE_SIZE } from "@/services/orderService";
import { defaultFilters, type DashboardFilters as Filters } from "@/types/dashboard";

export const Route = createFileRoute("/orders")({
  head: () => ({
    meta: [
      { title: "Production Orders | SEBN Production Dashboard" },
      {
        name: "description",
        content:
          "Browse welding production orders with planned, reported and scrapped quantities, backlog and progress.",
      },
      { property: "og:title", content: "Production Orders | SEBN Production Dashboard" },
      {
        property: "og:description",
        content: "Filter production orders by date, status and part number.",
      },
    ],
  }),
  component: OrdersPage,
});

function OrdersPage() {
  const [filters, setFilters] = useState<Filters>(defaultFilters);
  const [page, setPage] = useState(0);

  const { orders, totalElements, totalPages, isLoading, isFetching, error, refetch } =
    useOrdersData(filters, page, DEFAULT_ORDERS_PAGE_SIZE);

  const errorMessage =
    error instanceof Error ? error.message : API_ERROR_MESSAGE;

  const handleApplyFilters = (next: Filters) => {
    setFilters(next);
    setPage(0);
  };

  return (
    <DashboardShell
      title="Production Orders"
      subtitle="Review every production order with planned, reported and remaining quantities."
    >
      <DashboardFilters value={filters} onApply={handleApplyFilters} />
      {error && (
        <DashboardApiError
          message={errorMessage}
          onRetry={() => void refetch()}
          isRetrying={isFetching}
        />
      )}
      {isLoading ? (
        <OrdersTableSkeleton />
      ) : (
        <OrdersTable
          orders={orders}
          totalElements={totalElements}
          page={page}
          totalPages={totalPages}
          onPrevious={() => setPage((p) => Math.max(0, p - 1))}
          onNext={() => setPage((p) => Math.min(Math.max(totalPages - 1, 0), p + 1))}
        />
      )}
    </DashboardShell>
  );
}
