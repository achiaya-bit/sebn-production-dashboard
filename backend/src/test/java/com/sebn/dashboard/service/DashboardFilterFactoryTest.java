package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link DashboardFilterFactory} time-range handling.
 * No Spring context required.
 */
class DashboardFilterFactoryTest {

    // ── valid pairs ───────────────────────────────────────────────────────

    @Test
    void bothTimeValues_present_areIncluded() {
        DashboardFilter f = DashboardFilterFactory.from(null, null, null, null, "06:00", "14:00");
        assertThat(f.fromTime()).isEqualTo("06:00");
        assertThat(f.toTime()).isEqualTo("14:00");
        assertThat(f.hasTimeFilter()).isTrue();
    }

    @Test
    void bothTimeValues_null_filterHasNoTime() {
        DashboardFilter f = DashboardFilterFactory.from(null, null, null, null, null, null);
        assertThat(f.hasTimeFilter()).isFalse();
    }

    @Test
    void bothTimeValues_blank_filterHasNoTime() {
        DashboardFilter f = DashboardFilterFactory.from(null, null, null, null, "  ", "  ");
        assertThat(f.hasTimeFilter()).isFalse();
    }

    @Test
    void overnightRange_22_to_06_accepted() {
        DashboardFilter f = DashboardFilterFactory.from(null, null, null, null, "22:00", "06:00");
        assertThat(f.fromTime()).isEqualTo("22:00");
        assertThat(f.toTime()).isEqualTo("06:00");
    }

    @Test
    void equalBoundaries_06_to_06_accepted() {
        DashboardFilter f = DashboardFilterFactory.from(null, null, null, null, "06:00", "06:00");
        assertThat(f.fromTime()).isEqualTo("06:00");
        assertThat(f.toTime()).isEqualTo("06:00");
    }

    @Test
    void timeFilterCombinedWithDateAndStatus() {
        DashboardFilter f = DashboardFilterFactory.from(
                "20260701", "20260731", "50", "3AU", "14:00", "22:00");
        assertThat(f.startDate()).isEqualTo("20260701");
        assertThat(f.endDate()).isEqualTo("20260731");
        assertThat(f.status()).isEqualTo("50");
        assertThat(f.partNumber()).isEqualTo("3AU");
        assertThat(f.fromTime()).isEqualTo("14:00");
        assertThat(f.toTime()).isEqualTo("22:00");
        assertThat(f.hasTimeFilter()).isTrue();
    }

    // ── incomplete pairs — must throw 400 ─────────────────────────────────

    @Test
    void onlyFromTime_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "06:00", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fromTime and toTime must both be provided together");
    }

    @Test
    void onlyFromTime_withBlankTo_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "06:00", "  "))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void onlyToTime_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, null, "14:00"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fromTime and toTime must both be provided together");
    }

    @Test
    void onlyToTime_withBlankFrom_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "  ", "14:00"))
                .isInstanceOf(BadRequestException.class);
    }

    // ── invalid format — must throw 400 ──────────────────────────────────

    @Test
    void invalidFromTimeFormat_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "6:00", "14:00"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fromTime");
    }

    @Test
    void invalidToTimeFormat_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "06:00", "2400"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("toTime");
    }

    @Test
    void invalidHour_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "25:00", "06:00"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void invalidMinute_throwsBadRequest() {
        assertThatThrownBy(() ->
                DashboardFilterFactory.from(null, null, null, null, "06:00", "14:60"))
                .isInstanceOf(BadRequestException.class);
    }

    // ── empty result (filter present but non-matching) ────────────────────

    /**
     * The factory itself does not query the database — "empty result" is
     * demonstrated by verifying that a maximally restrictive filter is
     * constructed without error. The repository will return empty when
     * the database has no matching rows.
     */
    @Test
    void veryNarrowFilter_constructedWithoutError() {
        DashboardFilter f = DashboardFilterFactory.from(
                "20991231", "20991231", "50", "NONEXISTENT_PART_XYZ", "23:59", "23:59");
        assertThat(f.startDate()).isEqualTo("20991231");
        assertThat(f.endDate()).isEqualTo("20991231");
        assertThat(f.hasTimeFilter()).isTrue();
    }
}
