import {
  Bar,
  BarChart,
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

// ── Color tokens ─────────────────────────────────────────────────────────────
const COLOR_PLANNED  = "#94A3B8";   // slate-400 — grey bars
const COLOR_REPORTED = "#EA580C";   // orange-600 — line (multi) / bar (single)

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

/** Shared axis / tooltip / legend props to keep both render paths consistent. */
function sharedAxisProps(chartData: Array<{ label: string; displayDate: string }>) {
  return {
    xAxisProps: {
      dataKey: "label" as const,
      tick: { fontSize: 11, fill: "var(--color-muted-foreground)" } as const,
      axisLine: { stroke: "var(--color-border)" } as const,
      tickLine: false,
      interval: "preserveStartEnd" as const,
      minTickGap: 40,
    },
    yAxisProps: {
      tickFormatter: (v: number) => formatCompact(v),
      tick: { fontSize: 11, fill: "var(--color-muted-foreground)" } as const,
      axisLine: false,
      tickLine: false,
      width: 52,
    },
    tooltipProps: {
      contentStyle: {
        borderRadius: 8,
        border: "1px solid var(--color-border)",
        background: "var(--color-card)",
        fontSize: 12,
      },
      labelFormatter: (
        _label: string,
        payload: Array<{ payload?: (typeof chartData)[number] }>,
      ) => {
        const d = payload?.[0]?.payload;
        return d?.displayDate ?? _label;
      },
      formatter: (value: number, name: string) => [
        formatNumber(value),
        name === "Planned" ? "Planned" : "Reported",
      ],
    },
    legendProps: {
      iconType: "circle" as const,
      iconSize: 8,
      formatter: (name: string) => (
        <span style={{ fontSize: 11, color: "var(--color-muted-foreground)" }}>{name}</span>
      ),
    },
  };
}

export function PlannedVsReportedChart({ data, isLoading }: Props) {
  const chartData = data.map((p) => ({
    ...p,
    label: formatAxisDate(p.date),
    displayDate: /^\d{8}$/.test(p.date) ? formatDate(p.date) : p.date,
  }));

  const { xAxisProps, yAxisProps, tooltipProps, legendProps } = sharedAxisProps(chartData);

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
            {chartData.length === 1 ? (
              /*
               * Single-day view: a standard grouped BarChart.
               * A Line cannot be rendered from a single point, so two adjacent
               * bars give an immediately readable comparison instead.
               */
              <BarChart
                data={chartData}
                margin={{ top: 8, right: 8, left: 0, bottom: 0 }}
                barCategoryGap="30%"
                barGap={4}
              >
                <CartesianGrid vertical={false} stroke="var(--color-border)" />
                <XAxis {...xAxisProps} />
                <YAxis {...yAxisProps} />
                <Tooltip {...tooltipProps} />
                <Legend {...legendProps} />

                <Bar
                  dataKey="plannedQuantity"
                  name="Planned"
                  fill={COLOR_PLANNED}
                  fillOpacity={0.75}
                  radius={[3, 3, 0, 0]}
                />
                <Bar
                  dataKey="reportedQuantity"
                  name="Reported"
                  fill={COLOR_REPORTED}
                  fillOpacity={0.85}
                  radius={[3, 3, 0, 0]}
                />
              </BarChart>
            ) : (
              /*
               * Multi-day view: ComposedChart — grey bars (Planned) + orange
               * line (Reported).  The line is meaningful only when there are
               * at least two data points.
               */
              <ComposedChart
                data={chartData}
                margin={{ top: 8, right: 8, left: 0, bottom: 0 }}
              >
                <CartesianGrid vertical={false} stroke="var(--color-border)" />
                <XAxis {...xAxisProps} />
                <YAxis {...yAxisProps} />
                <Tooltip {...tooltipProps} />
                <Legend {...legendProps} />

                <Bar
                  dataKey="plannedQuantity"
                  name="Planned"
                  fill={COLOR_PLANNED}
                  fillOpacity={0.65}
                  barSize={12}
                  radius={[3, 3, 0, 0]}
                />
                <Line
                  type="monotone"
                  dataKey="reportedQuantity"
                  name="Reported"
                  stroke={COLOR_REPORTED}
                  strokeWidth={2.5}
                  dot={false}
                  activeDot={{ r: 5, fill: COLOR_REPORTED, stroke: "#fff", strokeWidth: 2 }}
                />
              </ComposedChart>
            )}
          </ResponsiveContainer>
        </div>
      )}
    </ChartPanel>
  );
}
