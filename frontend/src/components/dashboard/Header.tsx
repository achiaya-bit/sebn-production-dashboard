interface HeaderProps {
  title: string;
  subtitle: string;
}

export function Header({ title, subtitle }: HeaderProps) {
  const today = new Date().toLocaleDateString("en-GB", {
    weekday: "short",
    day: "2-digit",
    month: "short",
    year: "numeric",
  });

  return (
    <header className="grid grid-cols-[minmax(0,1fr)_auto] items-start gap-4 border-b border-border bg-card px-6 py-4 sm:flex sm:items-center sm:justify-between">
      <div className="min-w-0">
        <h1 className="truncate text-xl font-semibold tracking-tight text-foreground">{title}</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">{subtitle}</p>
      </div>
      <div className="flex shrink-0 flex-col items-end gap-1">
        <span className="inline-flex items-center gap-2 rounded-full border border-border bg-success-soft px-2.5 py-1 text-xs font-medium text-foreground">
          <span className="h-1.5 w-1.5 rounded-full bg-success" />
          System operational
        </span>
        <span className="text-xs text-muted-foreground tabular">{today}</span>
      </div>
    </header>
  );
}
