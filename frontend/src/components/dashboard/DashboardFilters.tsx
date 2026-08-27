import { useRef, useState } from "react";
import { CalendarDays, Clock, RotateCcw, Search, SlidersHorizontal } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { defaultFilters, type DashboardFilters as Filters } from "@/types/dashboard";
import type { OrderStatus } from "@/types/order";

interface Props {
  value: Filters;
  onApply: (filters: Filters) => void;
}

interface FilterGroupProps {
  title: string;
  icon?: React.ComponentType<{ className?: string }>;
  children: React.ReactNode;
}

function FilterGroup({ title, icon: Icon, children }: FilterGroupProps) {
  return (
    <div className="space-y-3">
      <div className="flex items-center gap-1.5 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">
        {Icon && <Icon className="h-3.5 w-3.5" aria-hidden="true" />}
        {title}
      </div>
      {children}
    </div>
  );
}

/**
 * Date input with a reliable single Lucide calendar icon.
 *
 * Problem: with the French locale placeholder "jj/mm/aaaa" the text fills
 * the native input width and pushes the webkit calendar indicator off the
 * right edge.
 *
 * Solution:
 *  1. Hide the webkit indicator via the scoped `.date-input-native` CSS class
 *     defined in styles.css. The hidden indicator still occupies the right
 *     edge so clicking that area still opens the picker natively.
 *  2. Render a single Lucide CalendarDays icon absolutely positioned on the
 *     right. It calls showPicker() (with a safe focus() fallback) on click.
 *  3. Right-padding (pr-10) keeps the date text clear of the icon.
 *  4. The input remains type="date" — native validation, keyboard nav and
 *     screen-reader labels are all preserved.
 */
interface DatePickerInputProps {
  id: string;
  value: string;
  onChange: (v: string) => void;
  label: string;
}

function DatePickerInput({ id, value, onChange, label }: DatePickerInputProps) {
  const inputRef = useRef<HTMLInputElement>(null);

  const openPicker = () => {
    const el = inputRef.current;
    if (!el) return;
    if ("showPicker" in el && typeof el.showPicker === "function") {
      try {
        el.showPicker();
      } catch {
        el.focus();
      }
    } else {
      el.focus();
    }
  };

  return (
    <div className="min-w-0 space-y-1.5">
      <Label htmlFor={id} className="text-xs text-muted-foreground">
        {label}
      </Label>
      {/* position:relative so the icon is pinned inside the field */}
      <div className="relative min-w-0">
        <input
          ref={inputRef}
          id={id}
          type="date"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          /*
           * date-input-native: hides the webkit calendar indicator via
           * scoped CSS in styles.css without affecting any other input.
           */
          className={[
            "date-input-native",
            "flex h-9 w-full min-w-0 rounded-md",
            "border border-input bg-transparent",
            "px-3 pr-10 py-1 text-sm text-foreground",
            "placeholder:text-muted-foreground",
            "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
            "disabled:cursor-not-allowed disabled:opacity-50",
          ].join(" ")}
        />
        {/* Lucide icon — the only visible calendar indicator per field */}
        <button
          type="button"
          aria-hidden="true"
          tabIndex={-1}
          onClick={openPicker}
          className="absolute right-0 top-0 flex h-full w-9 cursor-pointer items-center justify-center text-muted-foreground hover:text-foreground focus:outline-none"
        >
          <CalendarDays className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
}

/**
 * Validates that fromTime and toTime are both present or both absent.
 * Returns an error message string, or null when the pair is valid.
 */
function validateTimePair(from: string, to: string): string | null {
  const hasFrom = from.trim().length > 0;
  const hasTo = to.trim().length > 0;
  if (hasFrom && !hasTo) return "To Time is required when From Time is set.";
  if (!hasFrom && hasTo) return "From Time is required when To Time is set.";
  return null;
}

export function DashboardFilters({ value, onApply }: Props) {
  const [draft, setDraft] = useState<Filters>(value);
  const [timeError, setTimeError] = useState<string | null>(null);

  const set = <K extends keyof Filters>(key: K, v: Filters[K]) => {
    setDraft((prev) => ({ ...prev, [key]: v }));
    // Clear the time error as soon as the user edits a time field
    if (key === "fromTime" || key === "toTime") {
      setTimeError(null);
    }
  };

  const handleApply = () => {
    const err = validateTimePair(draft.fromTime, draft.toTime);
    if (err) {
      setTimeError(err);
      return;
    }
    setTimeError(null);
    onApply(draft);
  };

  const handleReset = () => {
    setDraft(defaultFilters);
    setTimeError(null);
    onApply(defaultFilters);
  };

  return (
    <section className="panel p-4">
      <div className="mb-4 flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
        <SlidersHorizontal className="h-3.5 w-3.5" aria-hidden="true" />
        Filters
      </div>

      {/*
        Desktop layout (xl):
          PERIOD            — 2 columns (wide enough for two date inputs)
          REPORTING TIME    — 1 column
          ADDITIONAL        — 1 column
          ACTIONS           — auto (shrinks to button width)

        Large desktop (2xl):
          All four content groups side-by-side.

        Mobile / tablet (below xl):
          Groups stack one per row; sub-grids go two-column at sm.
      */}
      <div className="grid gap-x-6 gap-y-5 xl:grid-cols-[minmax(360px,2fr)_minmax(200px,1fr)_minmax(200px,1fr)_auto]">

        {/* ── Period ─────────────────────────────────────────────────────── */}
        <FilterGroup title="Period">
          {/*
            Two date inputs side-by-side. Each needs ≥190 px for the French
            locale placeholder + the calendar icon. The parent column is at
            least 360 px (minmax above), split evenly here.
          */}
          <div className="grid min-w-0 gap-3 grid-cols-2">
            <DatePickerInput
              id="startDate"
              label="Start Date"
              value={draft.startDate}
              onChange={(v) => set("startDate", v)}
            />
            <DatePickerInput
              id="endDate"
              label="End Date"
              value={draft.endDate}
              onChange={(v) => set("endDate", v)}
            />
          </div>
        </FilterGroup>

        {/* ── Reporting Time Range ──────────────────────────────────────── */}
        <FilterGroup title="Reporting Time Range" icon={Clock}>
          <div className="space-y-2">
            <div className="grid min-w-0 gap-3 sm:grid-cols-2">
              <div className="min-w-0 space-y-1.5">
                <Label htmlFor="fromTime" className="text-xs text-muted-foreground">
                  From Time
                </Label>
                <input
                  id="fromTime"
                  type="time"
                  value={draft.fromTime}
                  onChange={(e) => set("fromTime", e.target.value)}
                  className={[
                    "flex h-9 w-full min-w-0 rounded-md",
                    "border bg-transparent",
                    timeError ? "border-critical" : "border-input",
                    "px-3 pr-10 py-1 text-sm text-foreground",
                    "placeholder:text-muted-foreground",
                    "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
                    "disabled:cursor-not-allowed disabled:opacity-50",
                  ].join(" ")}
                />
              </div>
              <div className="min-w-0 space-y-1.5">
                <Label htmlFor="toTime" className="text-xs text-muted-foreground">
                  To Time
                </Label>
                <input
                  id="toTime"
                  type="time"
                  value={draft.toTime}
                  onChange={(e) => set("toTime", e.target.value)}
                  className={[
                    "flex h-9 w-full min-w-0 rounded-md",
                    "border bg-transparent",
                    timeError ? "border-critical" : "border-input",
                    "px-3 pr-10 py-1 text-sm text-foreground",
                    "placeholder:text-muted-foreground",
                    "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
                    "disabled:cursor-not-allowed disabled:opacity-50",
                  ].join(" ")}
                />
              </div>
            </div>
            {timeError ? (
              <p className="text-[11px] font-medium text-critical" role="alert">
                {timeError}
              </p>
            ) : (
              <p className="text-[11px] text-muted-foreground/80">
                Optional. Filters by reporting time (WARMUZ). Leave both empty for all-day data.
              </p>
            )}
          </div>
        </FilterGroup>

        {/* ── Additional Filters ────────────────────────────────────────── */}
        <FilterGroup title="Additional Filters">
          <div className="grid min-w-0 gap-3 sm:grid-cols-2">
            <div className="min-w-0 space-y-1.5">
              <Label className="text-xs text-muted-foreground">Order Status</Label>
              <Select
                value={draft.status}
                onValueChange={(v) => set("status", v as OrderStatus | "ALL")}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="All statuses" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All statuses</SelectItem>
                  <SelectItem value="TRANSFERRED">New / Transferred</SelectItem>
                  <SelectItem value="IN_PROGRESS">In Progress</SelectItem>
                  <SelectItem value="COMPLETED">Completed</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="min-w-0 space-y-1.5">
              <Label htmlFor="partNumber" className="text-xs text-muted-foreground">
                Part Number
              </Label>
              <div className="relative min-w-0">
                <Search
                  className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                  aria-hidden="true"
                />
                <input
                  id="partNumber"
                  className={[
                    "flex h-9 w-full min-w-0 rounded-md",
                    "border border-input bg-transparent",
                    "pl-9 pr-3 py-1 text-sm text-foreground",
                    "placeholder:text-muted-foreground",
                    "focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring",
                    "disabled:cursor-not-allowed disabled:opacity-50",
                  ].join(" ")}
                  placeholder="Search part number..."
                  value={draft.partNumber}
                  onChange={(e) => set("partNumber", e.target.value)}
                />
              </div>
            </div>
          </div>
        </FilterGroup>

        {/* ── Actions ───────────────────────────────────────────────────── */}
        <div className="space-y-3">
          <div className="text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">
            Actions
          </div>
          <div className="flex flex-col gap-2 sm:flex-row xl:flex-col xl:items-stretch">
            <Button variant="outline" onClick={handleReset}>
              <RotateCcw className="h-4 w-4" aria-hidden="true" />
              Reset
            </Button>
            <Button onClick={handleApply}>Apply Filters</Button>
          </div>
        </div>

      </div>
    </section>
  );
}
