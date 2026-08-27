package com.sebn.dashboard.repository;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.entity.WaoOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class WaoOrderRepositoryImpl implements WaoOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigDecimal sumPlannedQuantity(DashboardFilter filter) {
        return sumDecimal(filter, "waurmg");
    }

    @Override
    public BigDecimal sumReportedQuantity(DashboardFilter filter) {
        return sumDecimal(filter, "wagfmg");
    }

    @Override
    public BigDecimal sumScrappedQuantity(DashboardFilter filter) {
        return sumDecimal(filter, "waaumg");
    }

    @Override
    public List<WaoOrderRepository.StatusCountProjection> countOrdersGroupedByWastat(DashboardFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<WaoOrder> root = query.from(WaoOrder.class);

        Expression<String> status = root.get("wastat");
        Expression<Long> orderCount = cb.count(root);

        query.multiselect(status.alias("status"), orderCount.alias("orderCount"));
        query.groupBy(status);
        applyFilter(query, root, cb, filter);

        return entityManager.createQuery(query).getResultList().stream()
                .map(tuple -> (WaoOrderRepository.StatusCountProjection) new StatusCountView(
                        tuple.get("status", String.class),
                        tuple.get("orderCount", Long.class)))
                .toList();
    }

    @Override
    public List<WaoOrderRepository.PartBacklogProjection> findTopPartNumbersByBacklog(
            DashboardFilter filter,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<WaoOrder> root = query.from(WaoOrder.class);

        Expression<String> partNumber = root.get("watenr");
        Expression<BigDecimal> backlog = cb.coalesce(
                cb.diff(cb.sum(root.get("waurmg")), cb.sum(root.get("wagfmg"))),
                BigDecimal.ZERO);

        query.multiselect(partNumber.alias("partNumber"), backlog.alias("backlog"));
        query.groupBy(partNumber);
        query.orderBy(cb.desc(backlog));
        applyFilter(query, root, cb, filter);

        var typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList().stream()
                .map(tuple -> (WaoOrderRepository.PartBacklogProjection) new PartBacklogView(
                        tuple.get("partNumber", String.class),
                        tuple.get("backlog", BigDecimal.class)))
                .toList();
    }

    @Override
    public List<WaoOrderRepository.DailyProductionProjection> findDailyProductionByPlannedDate(
            DashboardFilter filter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<WaoOrder> root = query.from(WaoOrder.class);

        Expression<String> date = root.get("waente");
        Expression<BigDecimal> planned = cb.coalesce(cb.sum(root.get("waurmg")), BigDecimal.ZERO);
        Expression<BigDecimal> reported = cb.coalesce(cb.sum(root.get("wagfmg")), BigDecimal.ZERO);

        query.multiselect(date.alias("date"), planned.alias("plannedQuantity"), reported.alias("reportedQuantity"));
        query.groupBy(date);
        query.orderBy(cb.asc(date));
        applyFilter(query, root, cb, filter);

        return entityManager.createQuery(query).getResultList().stream()
                .map(tuple -> (WaoOrderRepository.DailyProductionProjection) new DailyProductionView(
                        tuple.get("date", String.class),
                        tuple.get("plannedQuantity", BigDecimal.class),
                        tuple.get("reportedQuantity", BigDecimal.class)))
                .toList();
    }

    private BigDecimal sumDecimal(DashboardFilter filter, String field) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<WaoOrder> root = query.from(WaoOrder.class);

        query.select(cb.coalesce(cb.sum(root.get(field)), BigDecimal.ZERO));
        applyFilter(query, root, cb, filter);

        BigDecimal result = entityManager.createQuery(query).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    private void applyFilter(
            CriteriaQuery<?> query,
            Root<WaoOrder> root,
            CriteriaBuilder cb,
            DashboardFilter filter) {

        Predicate predicate = WaoOrderSpecifications.buildPredicate(filter, root, cb);
        if (predicate != null) {
            query.where(predicate);
        }
    }

    private record StatusCountView(String status, Long orderCount)
            implements WaoOrderRepository.StatusCountProjection {

        @Override
        public String getStatus() {
            return status;
        }

        @Override
        public Long getOrderCount() {
            return orderCount;
        }
    }

    private record PartBacklogView(String partNumber, BigDecimal backlog)
            implements WaoOrderRepository.PartBacklogProjection {

        @Override
        public String getPartNumber() {
            return partNumber;
        }

        @Override
        public BigDecimal getBacklog() {
            return backlog;
        }
    }

    private record DailyProductionView(String date, BigDecimal plannedQuantity, BigDecimal reportedQuantity)
            implements WaoOrderRepository.DailyProductionProjection {

        @Override
        public String getDate() {
            return date;
        }

        @Override
        public BigDecimal getPlannedQuantity() {
            return plannedQuantity;
        }

        @Override
        public BigDecimal getReportedQuantity() {
            return reportedQuantity;
        }
    }
}
