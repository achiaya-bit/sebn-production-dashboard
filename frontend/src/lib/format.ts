const numberFormatter = new Intl.NumberFormat("en-US");

export const formatNumber = (value: number) => numberFormatter.format(value);

export const formatCompact = (value: number) =>
  new Intl.NumberFormat("en-US", { notation: "compact", maximumFractionDigits: 1 }).format(value);

export const formatPercent = (value: number) => `${value.toFixed(2)}%`;

export const formatDate = (iso: string) =>
  new Date(`${iso}T00:00:00`).toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
