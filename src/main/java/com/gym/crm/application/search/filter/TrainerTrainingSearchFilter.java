package com.gym.crm.application.search.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TrainerTrainingSearchFilter extends TrainingSearchFilter {
    private final String traineeName;
}