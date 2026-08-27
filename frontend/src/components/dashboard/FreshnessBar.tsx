import { RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import type { DataFreshnessResponse } from "@/types/dashboard";

interface Props {
  freshness: DataFreshnessResponse | null;
  /** ISO-8601 string set by the parent when the last successful fetch completed. */
  lastRefreshedAt: string | null;
  isFetching: boolean;
  onRefresh: () => void;
}

/**
 * Compact information bar displayed below the filter panel on the Dashboard.
 *
 * Shows:
 *  - Data source label ("Imported dataset" when dataMode is IMPORTED)
 *  - Latest reporting timestamp from the database (WARMDA + WARMUZ)
 *  - When the Dashboard last fetched data from the API
 *  - Auto-refresh cadence
 *  - Manual Refresh button
 *
 * Rules:
 *  - "Latest reporting data" comes only from the backend freshness endpoint.
 *  - "Dashboard refreshed" is the client-side fetch completion time, not
 *    the dataset timestamp.
 *  - Never displays "Live" when dataMode is IMPORTED.
 *  - null dates are shown as "Not available".
 */
export function FreshnessBar({ freshness, lastRefreshedAt, isFetching, onRefresh }: Props) {
  const dataMode = freshness?.dataMode ?? "IMPORTED";
  const isImported = dataMode !== "LIVE";

  const sourceLabel = isImported ? "Imported dataset" : "Live source";

  const reportedLabel = formatIsoDateTime(freshness?.latestReportedAt ?? null);
  const refreshedLabel = formatIsoDateTime(lastRefreshedAt);

  return (
    <div className="flex flex-wrap items-center justify-between gap-x-6 gap-y-2 rounded-md border border-border bg-card px-4 py-2.5 text-xs text-muted-foreground">
      {/* Left: dataset metadata */}
      <ul className="flex flex-wrap items-center gap-x-5 gap-y-1">
        <li className="flex items-center gap-1.5">
          <span className="font-medium text-foreground">Data source:</span>
          {sourceLabel}
        </li>
        <li className="flex items-center gap-1.5">
          <span className="font-medium text-foreground">Latest reporting data:</span>
          {reportedLabel}
        </li>
        <li className="flex items-center gap-1.5">
          <span className="font-medium text-foreground">Dashboard refreshed:</span>
          {refreshedLabel}
        </li>
        <li className="flex items-center gap-1.5">
          <span className="font-medium text-foreground">Auto-refresh:</span>
          60 s
        </li>
      </ul>

      {/* Right: manual Refresh button */}
      <Button
        variant="ghost"
        size="sm"
        onClick={onRefresh}
        disabled={isFetching}
        aria-label="Refresh dashboard data"
        className="h-7 gap-1.5 px-2 text-xs"
      >
        <RefreshCw
          className={["h-3.5 w-3.5", isFetching ? "animate-spin" : ""].join(" ")}
          aria-hidden="true"
        />
        Refresh
      </Button>
    </div>
  );
}

/**
 * Formats an ISO-8601 string (e.g. "2026-08-27T05:17:31" or an ISO Date string)
 * using the browser locale. Returns "Not available" when the value is null.
 */
function formatIsoDateTime(value: string | null | undefined): string {
  if (!value) return "Not available";
  try {
    // Handle both "2026-08-27T05:17:31" and "2026-08-27" gracefully.
    const d = new Date(value.length === 10 ? `${value}T00:00:00` : value);
    if (isNaN(d.getTime())) return "Not available";
    return d.toLocaleString(undefined, {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  } catch {
    return "Not available";
  }
}
