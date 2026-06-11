package com.gym.crm.platform.search;

import com.gym.crm.platform.search.filter.TrainingSearchFilter;
import com.gym.crm.platform.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class TrainingQueryBuilder<T extends TrainingSearchFilter> {

    public CriteriaQuery<Training> build(CriteriaBuilder cb, T filter) {
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        addUsernamePredicate(cb, root, filter, predicates);
        addDateRangePredicates(cb, root, filter, predicates);
        addSpecificPredicates(cb, root, filter, predicates);

        query.select(root)
                .where(cb.and(predicates.toArray(Predicate[]::new)));

        return query;
    }

    protected abstract Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username);

    protected abstract void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root, T filter, List<Predicate> predicates);

    private void addUsernamePredicate(CriteriaBuilder cb, Root<Training> root, T filter, List<Predicate> predicates) {
        Optional.ofNullable(filter.getUsername())
                .filter(username -> !username.isBlank())
                .ifPresent(username -> predicates.add(getUsernamePredicate(cb, root, username)));
    }

    private void addDateRangePredicates(CriteriaBuilder cb, Root<Training> root, T filter, List<Predicate> predicates) {
        Optional.ofNullable(filter.getFromDate())
                .ifPresent(fromDate -> predicates.add(
                        cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate)));

        Optional.ofNullable(filter.getToDate())
                .ifPresent(toDate -> predicates.add(
                        cb.lessThanOrEqualTo(root.get("trainingDate"), toDate)));
    }

    protected void addFullNamePredicate(CriteriaBuilder cb, Root<Training> root,
                                        List<Predicate> predicates, String fullName, String userPath) {
        Optional.ofNullable(fullName)
                .filter(value -> !value.isBlank())
                .ifPresent(value -> {
                    Join<?, ?> userJoin = resolveJoinPath(root, userPath);

                    Expression<String> fullNameExpression = cb.concat(
                            cb.concat(userJoin.get("firstName"), " "),
                            userJoin.get("lastName"));

                    predicates.add(cb.equal(fullNameExpression, value));
                });
    }

    private Join<?, ?> resolveJoinPath(Root<Training> root, String path) {
        String[] parts = path.split("\\.");

        Join<?, ?> join = root.join(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            join = join.join(parts[i]);
        }

        return join;
    }
}