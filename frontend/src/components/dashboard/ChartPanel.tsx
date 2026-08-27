import type { ReactNode } from "react";

interface ChartPanelProps {
  title: string;
  description?: string;
  children: ReactNode;
  className?: string;
}

export function ChartPanel({ title, description, children, className }: ChartPanelProps) {
  return (
    <section className={`panel flex flex-col p-4 ${className ?? ""}`}>
      <div className="mb-4 min-w-0">
        <h2 className="truncate text-sm font-semibold tracking-tight text-foreground">{title}</h2>
        {description && <p className="mt-0.5 text-xs text-muted-foreground">{description}</p>}
      </div>
      <div className="min-w-0 flex-1">{children}</div>
    </section>
  );
}
