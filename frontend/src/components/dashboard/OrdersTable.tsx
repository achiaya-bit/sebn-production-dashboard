import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { ProductionOrder } from "@/types/order";
import { formatDate, formatNumber } from "@/lib/format";
import { cn } from "@/lib/utils";
import { StatusBadge } from "./StatusBadge";

interface OrdersTableProps {
  orders: ProductionOrder[];
  totalElements?: number;
  page?: number;
  totalPages?: number;
  onPrevious?: () => void;
  onNext?: () => void;
}

export function OrdersTable({
  orders,
  totalElements = orders.length,
  page = 0,
  totalPages = 1,
  onPrevious,
  onNext,
}: OrdersTableProps) {
  const maxBacklog = Math.max(1, ...orders.map((o) => o.backlog));
  const showPagination = onPrevious != null && onNext != null;
  const displayPage = totalPages === 0 ? 0 : page + 1;
  const canPrevious = page > 0;
  const canNext = page < totalPages - 1;

  return (
    <section className="panel overflow-hidden">
      <div className="grid grid-cols-[minmax(0,1fr)_auto] items-center gap-4 border-b border-border px-4 py-3.5 sm:flex sm:justify-between">
        <div className="min-w-0">
          <h2 className="truncate text-sm font-semibold tracking-tight text-foreground">
            Production Orders
          </h2>
          <p className="mt-0.5 text-xs text-muted-foreground">Showing filtered production orders</p>
        </div>
        <span className="tabular shrink-0 rounded border border-border bg-muted px-2 py-1 text-xs text-muted-foreground">
          {formatNumber(totalElements)} orders
        </span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full min-w-[1000px] border-collapse text-sm">
          <thead>
            <tr className="bg-surface text-left text-[11px] uppercase tracking-wider text-muted-foreground">
              <th className="px-4 py-2.5 font-semibold">Order Number</th>
              <th className="px-4 py-2.5 font-semibold">Part Number</th>
              <th className="px-4 py-2.5 font-semibold">Planned Date</th>
              <th className="px-4 py-2.5 text-right font-semibold">Planned Qty</th>
              <th className="px-4 py-2.5 text-right font-semibold">Reported Qty</th>
              <th className="px-4 py-2.5 text-right font-semibold">Scrapped Qty</th>
              <th className="px-4 py-2.5 text-right font-semibold">Backlog</th>
              <th className="px-4 py-2.5 font-semibold">Status</th>
              <th className="px-4 py-2.5 font-semibold">Progress</th>
              <th className="w-10 px-4 py-2.5" />
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => {
              const high = order.backlog > maxBacklog * 0.6;
              return (
                <tr
                  key={order.orderNumber}
                  className="border-t border-border transition-colors hover:bg-surface"
                >
                  <td className="px-4 py-3 font-medium text-foreground">{order.orderNumber}</td>
                  <td className="px-4 py-3 text-muted-foreground">{order.partNumber}</td>
                  <td className="tabular px-4 py-3 text-muted-foreground">
                    {formatDate(order.plannedDate)}
                  </td>
                  <td className="tabular px-4 py-3 text-right text-foreground">
                    {formatNumber(order.plannedQuantity)}
                  </td>
                  <td className="tabular px-4 py-3 text-right text-foreground">
                    {formatNumber(order.reportedQuantity)}
                  </td>
                  <td className="tabular px-4 py-3 text-right text-muted-foreground">
                    {formatNumber(order.scrappedQuantity)}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <span
                      className={cn(
                        "tabular font-semibold",
                        order.backlog === 0
                          ? "text-success"
                          : high
                            ? "rounded bg-critical-soft px-2 py-0.5 text-critical"
                            : "text-warning-foreground",
                      )}
                    >
                      {formatNumber(order.backlog)}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <StatusBadge status={order.status} />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      <div className="h-1.5 w-24 overflow-hidden rounded-full bg-muted">
                        <div
                          className={cn(
                            "h-full rounded-full",
                            order.progress >= 100 ? "bg-success" : "bg-primary",
                          )}
                          style={{ width: `${Math.min(order.progress, 100)}%` }}
                        />
                      </div>
                      <span className="tabular w-12 text-xs text-muted-foreground">
                        {order.progress.toFixed(1)}%
                      </span>
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <button
                      type="button"
                      aria-label={`View details for ${order.orderNumber}`}
                      className="grid h-7 w-7 place-items-center rounded text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                    >
                      <ChevronRight className="h-4 w-4" />
                    </button>
                  </td>
                </tr>
              );
            })}
            {orders.length === 0 && (
              <tr className="border-t border-border">
                <td colSpan={10} className="px-4 py-10 text-center text-sm text-muted-foreground">
                  No production orders match the selected filters.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {showPagination && (
        <div className="flex flex-wrap items-center justify-between gap-3 border-t border-border px-4 py-3">
          <p className="text-xs text-muted-foreground">
            {formatNumber(totalElements)} orders total
          </p>
          <div className="flex items-center gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={onPrevious}
              disabled={!canPrevious}
            >
              <ChevronLeft className="h-4 w-4" />
              Previous
            </Button>
            <span className="tabular px-2 text-xs text-muted-foreground">
              Page {displayPage} of {Math.max(totalPages, 1)}
            </span>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={onNext}
              disabled={!canNext}
            >
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </section>
  );
}
