import { AlertCircle, Loader2, RotateCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";

interface DashboardApiErrorProps {
  message: string;
  onRetry?: () => void;
  isRetrying?: boolean;
}

export function DashboardApiError({ message, onRetry, isRetrying }: DashboardApiErrorProps) {
  return (
    <div className="panel flex flex-wrap items-center justify-between gap-3 border-critical/30 bg-critical-soft px-4 py-3 text-sm text-critical">
      <div className="flex items-center gap-3">
        <AlertCircle className="h-4 w-4 shrink-0" />
        <p>{message}</p>
      </div>
      {onRetry && (
        <Button
          type="button"
          variant="outline"
          size="sm"
          onClick={onRetry}
          disabled={isRetrying}
          className="border-critical/30 bg-background text-critical hover:bg-background/80"
        >
          <RotateCw className={isRetrying ? "h-4 w-4 animate-spin" : "h-4 w-4"} />
          Retry
        </Button>
      )}
    </div>
  );
}

export function DashboardLoadingOverlay({ label = "Loading production data…" }: { label?: string }) {
  return (
    <div className="panel flex items-center justify-center gap-2 px-4 py-8 text-sm text-muted-foreground">
      <Loader2 className="h-4 w-4 animate-spin" />
      {label}
    </div>
  );
}

export function KpiCardsSkeleton() {
  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="panel space-y-3 p-4">
          <Skeleton className="h-3 w-24" />
          <Skeleton className="h-8 w-32" />
          <Skeleton className="h-3 w-40" />
        </div>
      ))}
    </div>
  );
}

export function ChartsSkeleton() {
  return (
    <div className="panel p-4">
      <Skeleton className="mb-2 h-4 w-40" />
      <Skeleton className="mb-4 h-3 w-56" />
      <Skeleton className="h-[280px] w-full" />
    </div>
  );
}

export function OrdersTableSkeleton() {
  return (
    <div className="panel overflow-hidden p-4">
      <Skeleton className="mb-4 h-4 w-48" />
      <div className="space-y-2">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton key={i} className="h-10 w-full" />
        ))}
      </div>
    </div>
  );
}
