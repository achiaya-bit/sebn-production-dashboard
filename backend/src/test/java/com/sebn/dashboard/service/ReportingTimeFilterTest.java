package com.sebn.dashboard.service;

import com.sebn.dashboard.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ReportingTimeFilter}.
 * No Spring context or database required.
 */
class ReportingTimeFilterTest {

    // ── parseHhMm — valid ─────────────────────────────────────────────────

    @Test
    void parseHhMm_midnight_returnsZero() {
        assertThat(ReportingTimeFilter.parseHhMm("00:00", "field")).isZero();
    }

    @Test
    void parseHhMm_06_00_returnsCorrectSeconds() {
        // 6 * 3600 = 21600
        assertThat(ReportingTimeFilter.parseHhMm("06:00", "field")).isEqualTo(6 * 3600);
    }

    @Test
    void parseHhMm_14_00_returnsCorrectSeconds() {
        assertThat(ReportingTimeFilter.parseHhMm("14:00", "field")).isEqualTo(14 * 3600);
    }

    @Test
    void parseHhMm_22_00_returnsCorrectSeconds() {
        assertThat(ReportingTimeFilter.parseHhMm("22:00", "field")).isEqualTo(22 * 3600);
    }

    @Test
    void parseHhMm_23_59_returnsCorrectSeconds() {
        int expected = 23 * 3600 + 59 * 60;
        assertThat(ReportingTimeFilter.parseHhMm("23:59", "field")).isEqualTo(expected);
    }

    @Test
    void parseHhMm_withLeadingZeroHour_isAccepted() {
        // 05:30 → 5*3600 + 30*60
        assertThat(ReportingTimeFilter.parseHhMm("05:30", "field")).isEqualTo(5 * 3600 + 30 * 60);
    }

    // ── parseHhMm — invalid ───────────────────────────────────────────────

    @Test
    void parseHhMm_null_throwsBadRequest() {
        assertThatThrownBy(() -> ReportingTimeFilter.parseHhMm(null, "fromTime"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fromTime");
    }

    @Test
    void parseHhMm_blank_throwsBadRequest() {
        assertThatThrownBy(() -> ReportingTimeFilter.parseHhMm("   ", "fromTime"))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"24:00", "25:00", "99:99"})
    void parseHhMm_invalidHour_throwsBadRequest(String value) {
        assertThatThrownBy(() -> ReportingTimeFilter.parseHhMm(value, "fromTime"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fromTime");
    }

    @ParameterizedTest
    @ValueSource(strings = {"06:60", "14:99", "00:61"})
    void parseHhMm_invalidMinute_throwsBadRequest(String value) {
        assertThatThrownBy(() -> ReportingTimeFilter.parseHhMm(value, "toTime"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("toTime");
    }

    @ParameterizedTest
    @ValueSource(strings = {"6:00", "6:0", "600", "06-00", "06:0", ":00", "06:"})
    void parseHhMm_malformedFormat_throwsBadRequest(String value) {
        assertThatThrownBy(() -> ReportingTimeFilter.parseHhMm(value, "field"))
                .isInstanceOf(BadRequestException.class);
    }

    // ── warmuzToSeconds ───────────────────────────────────────────────────

    @Test
    void warmuzToSeconds_fiveDigit_normalizesWithLeadingZero() {
        // 51731 → 051731 → 05:17:31 → 5*3600 + 17*60 + 31
        int expected = 5 * 3600 + 17 * 60 + 31;
        assertThat(ReportingTimeFilter.warmuzToSeconds("51731")).isEqualTo(expected);
    }

    @Test
    void warmuzToSeconds_sixDigit_parsesCorrectly() {
        // 104003 → 10:40:03
        int expected = 10 * 3600 + 40 * 60 + 3;
        assertThat(ReportingTimeFilter.warmuzToSeconds("104003")).isEqualTo(expected);
    }

    @Test
    void warmuzToSeconds_zero_returnsMinusOne() {
        assertThat(ReportingTimeFilter.warmuzToSeconds("0")).isEqualTo(-1);
    }

    @Test
    void warmuzToSeconds_null_returnsMinusOne() {
        assertThat(ReportingTimeFilter.warmuzToSeconds(null)).isEqualTo(-1);
    }

    @Test
    void warmuzToSeconds_blank_returnsMinusOne() {
        assertThat(ReportingTimeFilter.warmuzToSeconds("   ")).isEqualTo(-1);
    }

    @Test
    void warmuzToSeconds_nonNumeric_returnsMinusOne() {
        assertThat(ReportingTimeFilter.warmuzToSeconds("XPPS600")).isEqualTo(-1);
    }

    @Test
    void warmuzToSeconds_invalidTime_returnsMinusOne() {
        // 996000 → 99:60:00 → invalid
        assertThat(ReportingTimeFilter.warmuzToSeconds("996000")).isEqualTo(-1);
    }

    // ── interval semantics (documented via parseHhMm comparison) ─────────

    /**
     * Normal interval 06:00–14:00: fromSeconds < toSeconds.
     * A value at 10:00 should be >= from AND < to.
     */
    @Test
    void normalInterval_06_to_14_semantics() {
        int from = ReportingTimeFilter.parseHhMm("06:00", "f");
        int to   = ReportingTimeFilter.parseHhMm("14:00", "t");
        int mid  = ReportingTimeFilter.parseHhMm("10:00", "x");

        assertThat(from).isLessThan(to);   // normal interval
        assertThat(mid).isGreaterThanOrEqualTo(from);
        assertThat(mid).isLessThan(to);
    }

    /**
     * Normal interval 14:00–22:00.
     */
    @Test
    void normalInterval_14_to_22_semantics() {
        int from = ReportingTimeFilter.parseHhMm("14:00", "f");
        int to   = ReportingTimeFilter.parseHhMm("22:00", "t");

        assertThat(from).isLessThan(to);   // normal interval
    }

    /**
     * Overnight interval 22:00–06:00: fromSeconds > toSeconds.
     * The predicate should use OR (>= from OR < to).
     */
    @Test
    void overnightInterval_22_to_06_fromGreaterThanTo() {
        int from = ReportingTimeFilter.parseHhMm("22:00", "f");
        int to   = ReportingTimeFilter.parseHhMm("06:00", "t");

        // from > to signals an overnight interval to the predicate builder
        assertThat(from).isGreaterThan(to);
    }

    /**
     * Interval 00:00–06:00: fromSeconds < toSeconds (normal interval spanning midnight start).
     */
    @Test
    void interval_00_to_06_isNormal() {
        int from = ReportingTimeFilter.parseHhMm("00:00", "f");
        int to   = ReportingTimeFilter.parseHhMm("06:00", "t");

        assertThat(from).isLessThan(to);
    }

    /**
     * Equal boundaries (e.g. 06:00–06:00): full-day flag triggers a
     * conjunction (no time restriction beyond valid WARMDA/WARMUZ).
     */
    @Test
    void equalBoundaries_fromEqualsTo() {
        int from = ReportingTimeFilter.parseHhMm("06:00", "f");
        int to   = ReportingTimeFilter.parseHhMm("06:00", "t");

        assertThat(from).isEqualTo(to);
    }
}
