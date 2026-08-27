import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { BacklogTrendPoint } from "@/types/dashboard";
import { formatCompact, formatDate, formatNumber } from "@/lib/format";
import { ChartPanel } from "./ChartPanel";
import { Skeleton } from "@/components/ui/skeleton";

interface Props {
  data: BacklogTrendPoint[];
  isLoading: boolean;
}

function formatAxisDate(raw: string): string {
  if (/^\d{8}$/.test(raw)) {
    return `${raw.slice(4, 6)}/${raw.slice(6, 8)}`;
  }
  return raw;
}

function EmptyState() {
  return (
    <div className="flex h-[280px] flex-col items-center justify-center gap-2 text-muted-foreground">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        className="h-8 w-8 opacity-30"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        strokeWidth={1.5}
        aria-hidden="true"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M3 17l4-8 4 4 4-6 4 10M3 21h18"
        />
      </svg>
      <p className="text-sm">No data available for the selected filters</p>
    </div>
  );
}

export function CumulativeBacklogChart({ data, isLoading }: Props) {
  const chartData = data.map((p) => ({
    ...p,
    label: formatAxisDate(p.date),
    displayDate: /^\d{8}$/.test(p.date) ? formatDate(p.date) : p.date,
  }));

  return (
    <ChartPanel
      title="Cumulative Backlog Trend"
      description="Evolution of the remaining production quantity"
    >
      {isLoading ? (
        <div className="space-y-3">
          <Skeleton className="h-4 w-32" />
          <Skeleton className="h-[280px] w-full" />
        </div>
      ) : chartData.length === 0 ? (
        <EmptyState />
      ) : (
        <div className="h-[280px]">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={chartData} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="gradCumulativeBacklog" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="var(--color-critical)" stopOpacity={0.22} />
                  <stop offset="95%" stopColor="var(--color-critical)" stopOpacity={0.02} />
                </linearGradient>
              </defs>

              <CartesianGrid vertical={false} stroke="var(--color-border)" />

              <XAxis
                dataKey="label"
                tick={{ fontSize: 11, fill: "var(--color-muted-foreground)" }}
                axisLine={{ stroke: "var(--color-border)" }}
                tickLine={false}
                interval="preserveStartEnd"
                minTickGap={40}
              />
              <YAxis
                tickFormatter={(v: number) => formatCompact(v)}
                tick={{ fontSize: 11, fill: "var(--color-muted-foreground)" }}
                axisLine={false}
                tickLine={false}
                width={52}
              />

              <Tooltip
                contentStyle={{
                  borderRadius: 8,
                  border: "1px solid var(--color-border)",
                  background: "var(--color-card)",
                  fontSize: 12,
                }}
                labelFormatter={(_label, payload) => {
                  const d = payload?.[0]?.payload as (typeof chartData)[number] | undefined;
                  return d?.displayDate ?? _label;
                }}
                formatter={(value: number, name: string) => {
                  const labels: Record<string, string> = {
                    cumulativeBacklog: "Cumulative Backlog",
                    dailyDifference: "Daily Difference",
                    plannedQuantity: "Planned",
                    reportedQuantity: "Reported",
                  };
                  return [formatNumber(value), labels[name] ?? name];
                }}
              />

              <Area
                type="monotone"
                dataKey="cumulativeBacklog"
                name="cumulativeBacklog"
                stroke="var(--color-critical)"
                strokeWidth={2.5}
                fill="url(#gradCumulativeBacklog)"
                dot={false}
                activeDot={{ r: 4 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      )}
    </ChartPanel>
  );
}
