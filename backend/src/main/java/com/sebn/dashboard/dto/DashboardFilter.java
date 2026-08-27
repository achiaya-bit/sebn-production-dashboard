package com.sebn.dashboard.dto;

/**
 * Optional dashboard / order filter criteria.
 * Date values use the database format {@code YYYYMMDD}.
 * Time values use {@code HH:mm} when a reporting time range is active.
 */
public record DashboardFilter(
        String startDate,
        String endDate,
        String status,
        String partNumber,
        String fromTime,
        String toTime) {

    public static DashboardFilter empty() {
        return new DashboardFilter(null, null, null, null, null, null);
    }

    public boolean hasTimeFilter() {
        return fromTime != null && toTime != null;
    }

    public boolean isEmpty() {
        return startDate == null
                && endDate == null
                && status == null
                && partNumber == null
                && !hasTimeFilter();
    }
}
