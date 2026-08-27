package com.sebn.dashboard.service;

import com.sebn.dashboard.dto.BacklogTrendDTO;
import com.sebn.dashboard.repository.WaoOrderRepository.DailyProductionProjection;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TrendCalculator}.
 * No Spring context or database required — all input is constructed in-process.
 *
 * Cumulative backlog formula (per spec):
 *   current = previous + planned - reported
 *   cumulativeBacklog(t) = MAX(0, current)
 */
class TrendCalculatorTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    private static DailyProductionProjection row(String date, double planned, double reported) {
        return new DailyProductionProjection() {
            @Override public String getDate()                  { return date; }
            @Override public BigDecimal getPlannedQuantity()   { return BigDecimal.valueOf(planned); }
            @Override public BigDecimal getReportedQuantity()  { return BigDecimal.valueOf(reported); }
        };
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    /** Empty input must return an empty list without throwing. */
    @Test
    void emptyInput_returnsEmptyList() {
        assertThat(TrendCalculator.cumulativeBacklogTrend(List.of())).isEmpty();
    }

    /** Single row: cumulative = MAX(0, 0 + planned - reported). */
    @Test
    void singleRow_cumulativeEqualsBacklog() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(
                List.of(row("20260701", 1000, 800)));

        assertThat(result).hasSize(1);
        BacklogTrendDTO dto = result.get(0);
        assertThat(dto.getDate()).isEqualTo("20260701");
        assertThat(dto.getPlannedQuantity()).isEqualByComparingTo("1000");
        assertThat(dto.getReportedQuantity()).isEqualByComparingTo("800");
        assertThat(dto.getDailyDifference()).isEqualByComparingTo("200");
        assertThat(dto.getCumulativeBacklog()).isEqualByComparingTo("200");
    }

    /**
     * Multiple rows accumulate correctly.
     *
     * Day 1: 1000 - 800 = 200  → cumulative = 200
     * Day 2:  500 - 400 = 100  → cumulative = 300
     * Day 3:  300 - 600 = -300 → 300 + (-300) = 0 → MAX(0, 0) = 0
     */
    @Test
    void multipleRows_accumulatesCorrectly() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 1000, 800),
                row("20260702",  500, 400),
                row("20260703",  300, 600)));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCumulativeBacklog()).isEqualByComparingTo("200");
        assertThat(result.get(1).getCumulativeBacklog()).isEqualByComparingTo("300");
        assertThat(result.get(2).getCumulativeBacklog()).isEqualByComparingTo("0");
    }

    /** Reported far exceeds planned: cumulative must floor at zero, never go negative. */
    @Test
    void backlogNeverGoesNegative() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(
                List.of(row("20260701", 100, 500)));

        assertThat(result.get(0).getCumulativeBacklog()).isEqualByComparingTo("0");
    }

    /** Reported always exceeds planned across multiple days: cumulative stays at zero throughout. */
    @Test
    void reportedAlwaysExceedsPlanned_cumulativeRemainsZero() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 100, 200),
                row("20260702", 100, 300),
                row("20260703", 100, 400)));

        result.forEach(dto ->
                assertThat(dto.getCumulativeBacklog())
                        .as("day %s must be >= 0", dto.getDate())
                        .isGreaterThanOrEqualTo(BigDecimal.ZERO));
    }

    /**
     * Excess reported production does NOT carry over as negative credit.
     *
     * Day 1: 0 + 100 - 500 = -400 → clamped to 0
     * Day 2: 0 + 600 - 200 = 400  (previous is 0, not -400)
     */
    @Test
    void floorResetsPreviousNegative_noNegativeCarryOver() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 100, 500),
                row("20260702", 600, 200)));

        assertThat(result.get(0).getCumulativeBacklog()).isEqualByComparingTo("0");
        assertThat(result.get(1).getCumulativeBacklog()).isEqualByComparingTo("400");
    }

    /** Output dates preserve the same order as the input list. */
    @Test
    void datesRetainInputOrder() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 100, 50),
                row("20260702", 200, 50),
                row("20260703", 150, 50)));

        assertThat(result)
                .extracting(BacklogTrendDTO::getDate)
                .containsExactly("20260701", "20260702", "20260703");
    }

    /** dailyDifference = planned - reported (raw, may be negative). */
    @Test
    void dailyDifferenceEqualsPlannedMinusReported() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 300, 100),
                row("20260702", 200, 250)));

        assertThat(result.get(0).getDailyDifference()).isEqualByComparingTo("200");
        assertThat(result.get(1).getDailyDifference()).isEqualByComparingTo("-50");
    }

    /** Zero-quantity rows leave cumulative unchanged. */
    @Test
    void zeroQuantityRows_noEffect() {
        List<BacklogTrendDTO> result = TrendCalculator.cumulativeBacklogTrend(List.of(
                row("20260701", 500, 200),
                row("20260702",   0,   0),
                row("20260703", 100,  50)));

        assertThat(result.get(0).getCumulativeBacklog()).isEqualByComparingTo("300");
        assertThat(result.get(1).getCumulativeBacklog()).isEqualByComparingTo("300");
        assertThat(result.get(2).getCumulativeBacklog()).isEqualByComparingTo("350");
    }
}
