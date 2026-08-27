import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import { STATUS_LABELS } from "@/lib/statusMapping";
import type { OrderStatusCount } from "@/types/dashboard";
import { formatNumber } from "@/lib/format";
import { ChartPanel } from "./ChartPanel";

const COLORS: Record<string, string> = {
  COMPLETED: "var(--color-success)",
  IN_PROGRESS: "var(--color-primary)",
  TRANSFERRED: "var(--color-warning)",
};

export function StatusChart({ data }: { data: OrderStatusCount[] }) {
  const total = data.reduce((sum, d) => sum + d.totalOrders, 0);
  const chartData = data.map((d) => ({ ...d, name: STATUS_LABELS[d.status] }));

  return (
    <ChartPanel title="Orders by Status" description="Production orders grouped by current status">
      <div className="relative h-[220px]">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={chartData}
              dataKey="totalOrders"
              nameKey="name"
              innerRadius={62}
              outerRadius={92}
              paddingAngle={2}
              stroke="var(--color-card)"
              strokeWidth={2}
            >
              {chartData.map((entry) => (
                <Cell key={entry.status} fill={COLORS[entry.status]} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                borderRadius: 8,
                border: "1px solid var(--color-border)",
                background: "var(--color-card)",
                fontSize: 12,
              }}
              formatter={(value: number) => [formatNumber(value), "Orders"]}
            />
          </PieChart>
        </ResponsiveContainer>
        <div className="pointer-events-none absolute inset-0 grid place-items-center">
          <div className="text-center">
            <p className="tabular text-xl font-semibold text-foreground">{formatNumber(total)}</p>
            <p className="text-[11px] uppercase tracking-wider text-muted-foreground">
              Total orders
            </p>
          </div>
        </div>
      </div>
      <ul className="mt-4 space-y-2 border-t border-border pt-3">
        {chartData.map((entry) => (
          <li key={entry.status} className="flex items-center justify-between gap-3 text-sm">
            <span className="flex min-w-0 items-center gap-2">
              <span
                className="h-2.5 w-2.5 shrink-0 rounded-sm"
                style={{ background: COLORS[entry.status] }}
              />
              <span className="truncate text-muted-foreground">{entry.name}</span>
            </span>
            <span className="tabular shrink-0 font-medium text-foreground">
              {formatNumber(entry.totalOrders)}
            </span>
          </li>
        ))}
      </ul>
    </ChartPanel>
  );
}
