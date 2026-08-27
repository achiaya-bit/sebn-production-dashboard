package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.exception.BadRequestException;
import java.util.regex.Pattern;

/**
 * Validates and normalizes optional dashboard filter request parameters.
 */
public final class DashboardFilterFactory {

    private static final Pattern YYYYMMDD = Pattern.compile("^\\d{8}$");

    private DashboardFilterFactory() {
    }

    /**
     * Builds a {@link DashboardFilter} from raw request parameters.
     * Blank / null values are treated as absent and do not restrict queries.
     *
     * @throws BadRequestException when a provided value is invalid
     */
    public static DashboardFilter from(
            String startDate,
            String endDate,
            String status,
            String partNumber,
            String fromTime,
            String toTime) {

        String normalizedStart = normalizeDate(startDate, "startDate");
        String normalizedEnd = normalizeDate(endDate, "endDate");
        String normalizedStatus = normalizeStatus(status);
        String normalizedPartNumber = normalizePartNumber(partNumber);
        String[] normalizedTimes = normalizeTimeRange(fromTime, toTime);

        if (normalizedStart != null && normalizedEnd != null && normalizedStart.compareTo(normalizedEnd) > 0) {
            throw new BadRequestException("startDate must not be after endDate");
        }

        return new DashboardFilter(
                normalizedStart,
                normalizedEnd,
                normalizedStatus,
                normalizedPartNumber,
                normalizedTimes[0],
                normalizedTimes[1]);
    }

    /** Backward-compatible overload without time parameters. */
    public static DashboardFilter from(String startDate, String endDate, String status, String partNumber) {
        return from(startDate, endDate, status, partNumber, null, null);
    }

    private static String[] normalizeTimeRange(String fromTime, String toTime) {
        boolean hasFrom = fromTime != null && !fromTime.isBlank();
        boolean hasTo = toTime != null && !toTime.isBlank();

        if (hasFrom != hasTo) {
            throw new BadRequestException("fromTime and toTime must both be provided together");
        }
        if (!hasFrom) {
            return new String[] {null, null};
        }

        String normalizedFrom = fromTime.trim();
        String normalizedTo = toTime.trim();
        ReportingTimeFilter.parseHhMm(normalizedFrom, "fromTime");
        ReportingTimeFilter.parseHhMm(normalizedTo, "toTime");
        return new String[] {normalizedFrom, normalizedTo};
    }

    private static String normalizeDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (!YYYYMMDD.matcher(trimmed).matches()) {
            throw new BadRequestException(fieldName + " must follow YYYYMMDD format");
        }
        return trimmed;
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String trimmed = status.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("status must not be blank when provided");
        }
        return trimmed;
    }

    private static String normalizePartNumber(String partNumber) {
        if (partNumber == null || partNumber.isBlank()) {
            return null;
        }
        return partNumber.trim();
    }
}
