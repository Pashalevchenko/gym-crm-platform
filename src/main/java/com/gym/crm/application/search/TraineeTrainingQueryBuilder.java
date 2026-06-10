package com.gym.crm.application.search;

import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.application.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class TraineeTrainingQueryBuilder extends TrainingQueryBuilder<TraineeTrainingSearchFilter> {

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username) {
        return cb.equal(root.join("trainee").join("user").get("username"), username);
    }

    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root,
                                         TraineeTrainingSearchFilter filter, List<Predicate> predicates) {
        addTrainerNamePredicate(cb, root, filter, predicates);
        addTrainingTypePredicate(cb, root, filter, predicates);
    }

    private void addTrainerNamePredicate(CriteriaBuilder cb, Root<Training> root,
                                         TraineeTrainingSearchFilter filter, List<Predicate> predicates) {
        addFullNamePredicate(cb, root, predicates, filter.getTrainerName(), "trainer.user");
    }

    private void addTrainingTypePredicate(CriteriaBuilder cb, Root<Training> root,
                                          TraineeTrainingSearchFilter filter, List<Predicate> predicates) {
        Optional.ofNullable(filter.getTrainingTypeName())
                .filter(trainingTypeName -> !trainingTypeName.isBlank())
                .ifPresent(trainingTypeName -> predicates.add(
                        cb.equal(root.get("trainingType").get("trainingTypeName"), trainingTypeName)));
    }
}