import type { ReactNode } from "react";
import { Header } from "./Header";
import { Sidebar } from "./Sidebar";

interface Props {
  title: string;
  subtitle: string;
  children: ReactNode;
}

export function DashboardShell({ title, subtitle, children }: Props) {
  return (
    <div className="flex min-h-screen flex-col bg-background lg:flex-row">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <Header title={title} subtitle={subtitle} />
        <main className="flex-1 space-y-4 p-4 lg:p-6">{children}</main>
      </div>
    </div>
  );
}
