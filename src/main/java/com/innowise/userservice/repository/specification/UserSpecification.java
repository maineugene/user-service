package com.innowise.userservice.repository.specification;

import com.innowise.userservice.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {
    public static Specification<User> filterByFirstNameAndSurname(String name,
                                                                  String surname){
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (StringUtils.hasText(name)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder
                                .lower(root.get("name")), "%" + name.toLowerCase() + "%")
                );
            }

            if (StringUtils.hasText(surname)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder
                                .lower(root.get("surname")), "%" + surname.toLowerCase() + "%")
                );
            }

            return predicate;
        };
    }
}
