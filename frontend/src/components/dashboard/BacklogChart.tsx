import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { TopBacklogPart } from "@/types/dashboard";
import { formatCompact, formatNumber } from "@/lib/format";
import { ChartPanel } from "./ChartPanel";

export function BacklogChart({ data }: { data: TopBacklogPart[] }) {
  const max = data.length > 0 ? Math.max(...data.map((d) => d.backlog)) : 0;

  return (
    <ChartPanel
      title="Top 10 Parts by Backlog"
      description="Part numbers with the highest remaining quantity"
    >
      <div className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={data} layout="vertical" margin={{ left: 4, right: 16, top: 4 }}>
            <CartesianGrid horizontal={false} stroke="var(--color-border)" />
            <XAxis
              type="number"
              tickFormatter={(v: number) => formatCompact(v)}
              tick={{ fontSize: 11, fill: "var(--color-muted-foreground)" }}
              axisLine={false}
              tickLine={false}
            />
            <YAxis
              type="category"
              dataKey="partNumber"
              width={64}
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
              formatter={(value: number) => [formatNumber(value), "Backlog"]}
            />
            <Bar dataKey="backlog" radius={[0, 3, 3, 0]} barSize={16}>
              {data.map((entry) => (
                <Cell
                  key={entry.partNumber}
                  fill={
                    entry.backlog > max * 0.75
                      ? "var(--color-critical)"
                      : entry.backlog > max * 0.45
                        ? "var(--color-warning)"
                        : "var(--color-primary)"
                  }
                />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </ChartPanel>
  );
}
