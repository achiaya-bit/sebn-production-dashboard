/**
 * Client-side runtime error reporting.
 *
 * Currently a no-op stub. Replace with a real error-tracking integration
 * (e.g. Sentry, Datadog RUM) when required.
 */
export function reportError(error: unknown, _context: Record<string, unknown> = {}): void {
  if (typeof window === "undefined") return;
  // Forward to any globally registered error handler (e.g. a custom analytics
  // script) without coupling to a specific vendor SDK.
  if (process.env["NODE_ENV"] !== "production") {
    console.error("[reportError]", error);
  }
}
