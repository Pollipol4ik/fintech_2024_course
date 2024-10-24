package edu.kudago.specification;

import edu.kudago.repository.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventSpecification {

    public static Specification<EventEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<EventEntity> hasPlace(Long placeId) {
        return (root, query, criteriaBuilder) -> placeId == null ? null : criteriaBuilder.equal(root.get("place").get("id"), placeId);
    }

    public static Specification<EventEntity> betweenDates(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return null;
            } else if (fromDate != null && toDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), fromDate);
            } else if (fromDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("date"), toDate);
            } else {
                return criteriaBuilder.between(root.get("date"), fromDate, toDate);
            }
        };
    }
}
