package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.entity.WaoOrder;
import com.sebn.dashboard.exception.BadRequestException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates and applies reporting time range filters on {@code WARMUZ} / {@code WARMDA}.
 */
public final class ReportingTimeFilter {

    private static final Pattern HH_MM = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");

    private ReportingTimeFilter() {
    }

    public static int parseHhMm(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " must follow HH:mm format");
        }
        Matcher matcher = HH_MM.matcher(value.trim());
        if (!matcher.matches()) {
            throw new BadRequestException(fieldName + " must follow HH:mm format");
        }
        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        return hours * 3600 + minutes * 60;
    }

    public static int warmuzToSeconds(String warmuz) {
        if (warmuz == null || warmuz.isBlank() || "0".equals(warmuz.trim())) {
            return -1;
        }
        try {
            String padded = String.format("%06d", Integer.parseInt(warmuz.trim()));
            int hours = Integer.parseInt(padded.substring(0, 2));
            int minutes = Integer.parseInt(padded.substring(2, 4));
            int seconds = Integer.parseInt(padded.substring(4, 6));
            if (hours > 23 || minutes > 59 || seconds > 59) {
                return -1;
            }
            return hours * 3600 + minutes * 60 + seconds;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static Predicate buildPredicate(Root<WaoOrder> root, CriteriaBuilder cb, DashboardFilter filter) {
        if (filter == null || !filter.hasTimeFilter()) {
            return cb.conjunction();
        }

        int fromSeconds = parseHhMm(filter.fromTime(), "fromTime");
        int toSeconds = parseHhMm(filter.toTime(), "toTime");

        Predicate warmdaValid = cb.and(
                cb.isNotNull(root.get("warmda")),
                cb.notEqual(root.get("warmda"), "0"),
                cb.notEqual(root.get("warmda"), ""));

        Predicate warmuzValid = cb.and(
                cb.isNotNull(root.get("warmuz")),
                cb.notEqual(root.get("warmuz"), "0"),
                cb.notEqual(root.get("warmuz"), ""));

        boolean fullDay = filter.fromTime().equals(filter.toTime());
        Expression<Integer> reportingSeconds = reportingTimeSeconds(root, cb);

        Predicate timeInRange;
        if (fullDay) {
            timeInRange = cb.conjunction();
        } else if (fromSeconds >= toSeconds) {
            timeInRange = cb.or(
                    cb.greaterThanOrEqualTo(reportingSeconds, fromSeconds),
                    cb.lessThan(reportingSeconds, toSeconds));
        } else {
            timeInRange = cb.and(
                    cb.greaterThanOrEqualTo(reportingSeconds, fromSeconds),
                    cb.lessThan(reportingSeconds, toSeconds));
        }

        return cb.and(warmdaValid, warmuzValid, timeInRange);
    }

    private static Expression<Integer> reportingTimeSeconds(Root<WaoOrder> root, CriteriaBuilder cb) {
        Expression<String> padded = cb.function(
                "LPAD", String.class, root.get("warmuz"), cb.literal(6), cb.literal("0"));
        Expression<java.util.Date> parsed = cb.function(
                "STR_TO_DATE", java.util.Date.class, padded, cb.literal("%H%i%s"));
        return cb.function("TIME_TO_SEC", Integer.class, parsed);
    }
}
