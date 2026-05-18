package com.innowise.userservice.repository.specification;

import com.innowise.userservice.model.PaymentCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PaymentCardSpecification {
    public static Specification<PaymentCard> filterByHolder(String holder) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(holder)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder
                    .lower(root.get("holder")), "%" + holder.toLowerCase() + "%");
        };
    }
}
