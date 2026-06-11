package com.gym.crm.platform.repository.specification;

import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.platform.search.filter.TrainerTrainingSearchFilter;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrainingSpecifications {

    private static final String TRAINING_DATA = "trainingDate";

    public static Specification<Training> byTrainerCriteria(TrainerTrainingSearchFilter filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (StringUtils.isNotBlank(filter.getUsername())) {
                predicate = cb.and(predicate, cb.equal(root.get("trainer").get("user").get("username"), filter.getUsername()));
            }

            if (filter.getFromDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get(TRAINING_DATA), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get(TRAINING_DATA), filter.getToDate()));
            }

            if (StringUtils.isNotBlank(filter.getTraineeName())) {
                Join<Object, Object> traineeUser = root.join("trainee").join("user");

                var fullName = cb.concat(cb.concat(traineeUser.get("firstName"), " "), traineeUser.get("lastName"));
                predicate = cb.and(predicate, cb.like(cb.lower(fullName), "%" + filter.getTraineeName().toLowerCase() + "%"));
            }

            return predicate;
        };
    }

    public static Specification<Training> byTraineeCriteria(TraineeTrainingSearchFilter filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (StringUtils.isNotBlank(filter.getUsername())) {
                predicate = cb.and(predicate, cb.equal(root.get("trainee").get("user").get("username"), filter.getUsername()));
            }

            if (filter.getFromDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get(TRAINING_DATA), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get(TRAINING_DATA), filter.getToDate()));
            }

            if (StringUtils.isNotBlank(filter.getTrainerName())) {
                Join<Object, Object> trainerUser = root.join("trainer").join("user");

                var fullName = cb.concat(cb.concat(trainerUser.get("firstName"), " "), trainerUser.get("lastName"));
                predicate = cb.and(predicate, cb.like(cb.lower(fullName), "%" + filter.getTrainerName().toLowerCase() + "%"));
            }

            if (StringUtils.isNotBlank(filter.getTrainingTypeName())) {
                predicate = cb.and(predicate, cb.equal(root.get("trainingType").get("trainingTypeName"), filter.getTrainingTypeName()));
            }

            return predicate;
        };
    }
}