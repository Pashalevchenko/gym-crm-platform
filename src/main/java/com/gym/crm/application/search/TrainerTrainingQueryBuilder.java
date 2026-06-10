package com.gym.crm.application.search;

import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import com.gym.crm.application.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TrainerTrainingQueryBuilder extends TrainingQueryBuilder<TrainerTrainingSearchFilter> {

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username) {
        return cb.equal(root.join("trainer").join("user").get("username"), username);
    }

    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root,
                                         TrainerTrainingSearchFilter filter, List<Predicate> predicates) {
        addFullNamePredicate(cb, root, predicates, filter.getTraineeName(), "trainee.user");
    }
}