import type { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

interface KpiCardProps {
  label: string;
  value: string;
  hint: string;
  icon: LucideIcon;
  tone?: "neutral" | "primary" | "success" | "warning" | "critical";
  emphasis?: boolean;
  progress?: number;
}

const toneClasses = {
  neutral: "bg-muted text-muted-foreground",
  primary: "bg-primary-soft text-primary",
  success: "bg-success-soft text-success",
  warning: "bg-warning-soft text-warning",
  critical: "bg-critical-soft text-critical",
} as const;

export function KpiCard({
  label,
  value,
  hint,
  icon: Icon,
  tone = "neutral",
  emphasis = false,
  progress,
}: KpiCardProps) {
  return (
    <div
      className={cn(
        "panel relative flex flex-col gap-4 p-4",
        emphasis && "border-critical/35 shadow-[var(--shadow-elevated)]",
      )}
    >
      {emphasis && (
        <span className="absolute inset-x-0 top-0 h-0.5 rounded-t bg-critical" aria-hidden />
      )}
      <div className="flex items-start justify-between gap-3">
        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          {label}
        </p>
        <span className={cn("grid h-8 w-8 shrink-0 place-items-center rounded", toneClasses[tone])}>
          <Icon className="h-4 w-4" />
        </span>
      </div>
      <div>
        <p
          className={cn(
            "tabular text-2xl font-semibold tracking-tight text-foreground xl:text-[1.75rem]",
            emphasis && "text-critical",
          )}
        >
          {value}
        </p>
        {typeof progress === "number" && (
          <div className="mt-3 h-1.5 w-full overflow-hidden rounded-full bg-muted">
            <div
              className="h-full rounded-full bg-primary transition-all"
              style={{ width: `${Math.min(progress, 100)}%` }}
            />
          </div>
        )}
        <p className="mt-2 text-xs text-muted-foreground">{hint}</p>
      </div>
    </div>
  );
}
