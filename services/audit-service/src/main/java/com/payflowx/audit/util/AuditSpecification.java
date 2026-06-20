package com.payflowx.audit.util;

import com.payflowx.audit.dto.AuditSearchRequest;
import com.payflowx.audit.entity.AuditEvent;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AuditSpecification {

    private AuditSpecification() {
    }

    public static Specification<AuditEvent> search(AuditSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.sourceService() != null) {
                predicates.add(cb.equal(root.get("sourceService"), request.sourceService()));
            }
            if (request.eventType() != null) {
                predicates.add(cb.equal(root.get("eventType"), request.eventType()));
            }
            if (request.correlationId() != null) {
                predicates.add(cb.equal(root.get("correlationId"), request.correlationId()));
            }
            if (request.aggregateId() != null) {
                predicates.add(cb.equal(root.get("aggregateId"), request.aggregateId()));
            }
            if (request.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.fromDate()));
            }
            if (request.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.toDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}