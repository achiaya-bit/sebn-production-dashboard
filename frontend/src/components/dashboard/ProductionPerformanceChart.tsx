import { Bar, BarChart, CartesianGrid, Cell, LabelList, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { DashboardKpis } from "@/types/dashboard";
import { formatCompact, formatNumber } from "@/lib/format";
import { ChartPanel } from "./ChartPanel";

export function ProductionPerformanceChart({ kpis }: { kpis: DashboardKpis }) {
  const data = [
    { name: "Planned", value: kpis.plannedQuantity, fill: "var(--color-chart-5)" },
    { name: "Reported", value: kpis.reportedQuantity, fill: "var(--color-success)" },
    { name: "Backlog", value: kpis.backlog, fill: "var(--color-critical)" },
  ];

  return (
    <ChartPanel
      title="Production Performance"
      description="Planned vs reported quantity and resulting backlog"
    >
      <div className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} margin={{ top: 24, right: 8, left: 0 }}>
            <CartesianGrid vertical={false} stroke="var(--color-border)" />
            <XAxis
              dataKey="name"
              tick={{ fontSize: 12, fill: "var(--color-muted-foreground)" }}
              axisLine={{ stroke: "var(--color-border)" }}
              tickLine={false}
            />
            <YAxis
              tickFormatter={(v: number) => formatCompact(v)}
              tick={{ fontSize: 11, fill: "var(--color-muted-foreground)" }}
              axisLine={false}
              tickLine={false}
            />
            <Tooltip
              cursor={{ fill: "var(--color-muted)" }}
              contentStyle={{
                borderRadius: 8,
                border: "1px solid var(--color-border)",
                background: "var(--color-card)",
                fontSize: 12,
              }}
              formatter={(value: number) => [formatNumber(value), "Quantity"]}
            />
            <Bar dataKey="value" radius={[3, 3, 0, 0]} barSize={72}>
              <LabelList
                dataKey="value"
                position="top"
                formatter={(v: number) => formatCompact(v)}
                style={{ fill: "var(--color-foreground)", fontSize: 11, fontWeight: 600 }}
              />
              {data.map((entry) => (
                <Cell key={entry.name} fill={entry.fill} />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </ChartPanel>
  );
}
