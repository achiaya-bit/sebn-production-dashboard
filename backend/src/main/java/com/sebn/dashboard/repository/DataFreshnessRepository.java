package com.sebn.dashboard.repository;

import com.sebn.dashboard.entity.WaoOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Aggregation queries for dataset freshness metadata.
 *
 * <p>All aggregations are performed in MySQL — no order rows are loaded
 * into Java memory.
 *
 * <p><strong>WARMUZ normalization</strong>: the field is stored as a numeric
 * string of 5 or 6 characters (e.g. {@code 51731} = 05:17:31).
 * {@code LPAD(..., 6, '0')} normalizes it to six digits before parsing.
 *
 * <p><strong>WARMDA validity filter</strong>: rows where WARMDA is NULL,
 * blank, or the literal {@code "0"} are excluded.
 */
@Repository
public interface DataFreshnessRepository extends JpaRepository<WaoOrder, Integer> {

    /**
     * Returns the ISO-8601 local datetime string for the latest reporting entry.
     *
     * <p>SQL logic:
     * <ol>
     *   <li>Filter: WARMDA not null, not blank, not "0"; WARMUZ not null, not blank, not "0".</li>
     *   <li>Build a combined datetime: {@code CONCAT(WARMDA, LPAD(WARMUZ, 6, '0'))}
     *       gives {@code YYYYMMDDHHmmss}.</li>
     *   <li>Parse with {@code STR_TO_DATE(..., '%Y%m%d%H%i%s')}.</li>
     *   <li>Take the MAX and format as ISO-8601 ({@code '%Y-%m-%dT%H:%i:%s'}).</li>
     * </ol>
     *
     * @return ISO-8601 string such as {@code "2026-08-27T05:17:31"}, or {@code null}
     *         when no valid reporting entry exists.
     */
    @Query(value = """
            SELECT DATE_FORMAT(
                       MAX(STR_TO_DATE(
                               CONCAT(WARMDA, LPAD(WARMUZ, 6, '0')),
                               '%Y%m%d%H%i%s')),
                       '%Y-%m-%dT%H:%i:%s')
            FROM wao_orders
            WHERE WARMDA IS NOT NULL
              AND WARMDA <> ''
              AND WARMDA <> '0'
              AND WARMUZ IS NOT NULL
              AND WARMUZ <> ''
              AND WARMUZ <> '0'
            """, nativeQuery = true)
    String findLatestReportedAt();

    /**
     * Returns the maximum valid order modification date from {@code WAAEDA}.
     *
     * <p>Rows where WAAEDA is NULL, blank, or the literal {@code "0"} are
     * excluded. The value is returned as-is (YYYYMMDD string).
     *
     * @return YYYYMMDD string such as {@code "20260827"}, or {@code null}
     *         when no valid value exists.
     */
    @Query(value = """
            SELECT MAX(WAAEDA)
            FROM wao_orders
            WHERE WAAEDA IS NOT NULL
              AND WAAEDA <> ''
              AND WAAEDA <> '0'
            """, nativeQuery = true)
    String findLatestOrderModificationDate();
}
