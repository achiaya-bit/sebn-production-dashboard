import { STATUS_LABELS } from "@/lib/statusMapping";
import type { OrderStatus } from "@/types/order";
import { cn } from "@/lib/utils";

const styles: Record<OrderStatus, string> = {
  COMPLETED: "bg-success-soft text-success border-success/25",
  IN_PROGRESS: "bg-primary-soft text-primary border-primary/25",
  TRANSFERRED: "bg-warning-soft text-warning-foreground border-warning/30",
};

export function StatusBadge({ status }: { status: OrderStatus }) {
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1.5 whitespace-nowrap rounded border px-2 py-0.5 text-xs font-medium",
        styles[status],
      )}
    >
      <span className="h-1.5 w-1.5 rounded-full bg-current" />
      {STATUS_LABELS[status]}
    </span>
  );
}
