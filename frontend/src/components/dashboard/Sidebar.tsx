import { Link } from "@tanstack/react-router";
import { LayoutDashboard, ClipboardList, Factory } from "lucide-react";

const navItems = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard },
  { to: "/orders", label: "Production Orders", icon: ClipboardList },
] as const;

export function Sidebar() {
  return (
    <aside className="flex w-full shrink-0 flex-col bg-sidebar text-sidebar-foreground lg:h-screen lg:w-64 lg:sticky lg:top-0">
      <div className="flex items-center gap-3 border-b border-sidebar-border px-5 py-5">
        <div className="grid h-10 w-10 shrink-0 place-items-center rounded-md bg-sidebar-primary">
          <Factory className="h-5 w-5 text-sidebar-primary-foreground" />
        </div>
        <div className="min-w-0">
          <p className="truncate text-lg font-bold leading-tight tracking-[0.14em]">SEBN</p>
          <p className="truncate text-xs text-sidebar-foreground/60">Production Dashboard</p>
        </div>
      </div>

      <nav className="flex gap-1 overflow-x-auto p-3 lg:flex-col lg:overflow-visible">
        {navItems.map(({ to, label, icon: Icon }) => (
          <Link
            key={to}
            to={to}
            activeOptions={{ exact: true }}
            className="flex shrink-0 items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-sidebar-foreground/70 transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground data-[status=active]:bg-sidebar-accent data-[status=active]:text-sidebar-accent-foreground"
          >
            <Icon className="h-4 w-4 shrink-0" />
            <span>{label}</span>
          </Link>
        ))}
      </nav>

      <div className="mt-auto hidden border-t border-sidebar-border px-5 py-4 lg:block">
        <p className="text-xs font-semibold uppercase tracking-wider text-sidebar-foreground/80">
          Version 1
        </p>
        <p className="mt-0.5 text-xs text-sidebar-foreground/50">Production Monitoring</p>
      </div>
    </aside>
  );
}
