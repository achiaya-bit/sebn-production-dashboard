package com.sebn.dashboard.repository;

import com.sebn.dashboard.entity.WaoOrder;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WaoOrderRepository
        extends JpaRepository<WaoOrder, Integer>,
                JpaSpecificationExecutor<WaoOrder>,
                WaoOrderRepositoryCustom {

    List<WaoOrder> findByWastat(String wastat);

    /**
     * Total planned quantity (WAURMG) — unfiltered.
     */
    @Query("SELECT COALESCE(SUM(w.waurmg), 0) FROM WaoOrder w")
    BigDecimal getTotalPlannedQuantity();

    /**
     * Total reported / good finished quantity (WAGFMG) — unfiltered.
     */
    @Query("SELECT COALESCE(SUM(w.wagfmg), 0) FROM WaoOrder w")
    BigDecimal getTotalReportedQuantity();

    /**
     * Total scrapped quantity (WAAUMG) — unfiltered.
     */
    @Query("SELECT COALESCE(SUM(w.waaumg), 0) FROM WaoOrder w")
    BigDecimal getTotalScrappedQuantity();

    /**
     * Aggregate backlog: SUM(WAURMG - WAGFMG) — unfiltered.
     */
    @Query("SELECT COALESCE(SUM(w.waurmg - w.wagfmg), 0) FROM WaoOrder w")
    BigDecimal getBacklog();

    /**
     * Count of orders grouped by WASTAT — unfiltered.
     */
    @Query("""
            SELECT w.wastat AS status, COUNT(w) AS orderCount
            FROM WaoOrder w
            GROUP BY w.wastat
            """)
    List<StatusCountProjection> countOrdersGroupedByWastat();

    /**
     * Part numbers (WATENR) ordered by backlog descending — unfiltered.
     */
    @Query("""
            SELECT w.watenr AS partNumber,
                   COALESCE(SUM(w.waurmg - w.wagfmg), 0) AS backlog
            FROM WaoOrder w
            GROUP BY w.watenr
            ORDER BY COALESCE(SUM(w.waurmg - w.wagfmg), 0) DESC
            """)
    List<PartBacklogProjection> findTopPartNumbersByBacklog(Pageable pageable);

    interface StatusCountProjection {
        String getStatus();

        Long getOrderCount();
    }

    interface PartBacklogProjection {
        String getPartNumber();

        BigDecimal getBacklog();
    }

    interface DailyProductionProjection {
        String getDate();

        BigDecimal getPlannedQuantity();

        BigDecimal getReportedQuantity();
    }
}
