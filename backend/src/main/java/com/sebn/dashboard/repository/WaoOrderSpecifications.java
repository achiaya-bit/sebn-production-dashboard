package com.sebn.dashboard.repository;

import com.sebn.dashboard.dto.DashboardFilter;
import com.sebn.dashboard.entity.WaoOrder;
import com.sebn.dashboard.service.ReportingTimeFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for dynamic {@link WaoOrder} filtering.
 */
public final class WaoOrderSpecifications {

    private WaoOrderSpecifications() {
    }

    public static Specification<WaoOrder> withFilter(DashboardFilter filter) {
        return (root, query, cb) -> buildPredicate(filter, root, cb);
    }

    static Predicate buildPredicate(DashboardFilter filter, Root<WaoOrder> root, CriteriaBuilder cb) {
        if (filter == null || filter.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> predicates = new ArrayList<>();

        if (filter.startDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("waente"), filter.startDate()));
        }
        if (filter.endDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("waente"), filter.endDate()));
        }
        if (filter.status() != null) {
            predicates.add(cb.equal(root.get("wastat"), filter.status()));
        }
        if (filter.partNumber() != null) {
            predicates.add(cb.like(
                    cb.lower(root.get("watenr")),
                    "%" + filter.partNumber().toLowerCase() + "%"));
        }

        if (filter.hasTimeFilter()) {
            predicates.add(ReportingTimeFilter.buildPredicate(root, cb, filter));
        }

        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }
}
