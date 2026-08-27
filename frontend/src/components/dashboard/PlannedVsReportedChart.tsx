import {
  Bar,
  CartesianGrid,
  ComposedChart,
  Legend,
  Line,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { ProductionTrendPoint } from "@/types/dashboard";
import { formatCompact, formatDate, formatNumber } from "@/lib/format";
import { ChartPanel } from "./ChartPanel";
import { Skeleton } from "@/components/ui/skeleton";

interface Props {
  data: ProductionTrendPoint[];
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
        <path strokeLinecap="round" strokeLinejoin="round" d="M3 3v18h18M7 16l4-5 4 3 4-4" />
      </svg>
      <p className="text-sm">No data available for the selected filters</p>
    </div>
  );
}

export function PlannedVsReportedChart({ data, isLoading }: Props) {
  const chartData = data.map((p) => ({
    ...p,
    label: formatAxisDate(p.date),
    displayDate: /^\d{8}$/.test(p.date) ? formatDate(p.date) : p.date,
  }));

  return (
    <ChartPanel
      title="Planned vs Reported Over Time"
      description="Daily planned and reported quantities for the selected period"
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
            <ComposedChart data={chartData} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
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
                labelFormatter={(_label, payload: Array<{ payload?: (typeof chartData)[number] }>) => {
                  const d = payload?.[0]?.payload;
                  return d?.displayDate ?? _label;
                }}
                formatter={(value: number, name: string) => [
                  formatNumber(value),
                  name === "Planned" ? "Planned" : "Reported",
                ]}
              />

              <Legend
                iconType="circle"
                iconSize={8}
                formatter={(name: string) => (
                  <span style={{ fontSize: 11, color: "var(--color-muted-foreground)" }}>
                    {name}
                  </span>
                )}
              />

              {/* Planned: grey bars behind the Reported line */}
              <Bar
                dataKey="plannedQuantity"
                name="Planned"
                fill="#94A3B8"
                fillOpacity={0.65}
                barSize={12}
                radius={[3, 3, 0, 0]}
              />

              {/* Reported: solid burnt-orange line drawn on top of the bars */}
              <Line
                type="monotone"
                dataKey="reportedQuantity"
                name="Reported"
                stroke="#EA580C"
                strokeWidth={2.5}
                dot={false}
                activeDot={{ r: 5, fill: "#EA580C", stroke: "#fff", strokeWidth: 2 }}
              />
            </ComposedChart>
          </ResponsiveContainer>
        </div>
      )}
    </ChartPanel>
  );
}
