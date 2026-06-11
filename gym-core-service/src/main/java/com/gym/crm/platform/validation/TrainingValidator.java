package com.gym.crm.platform.validation;

import com.gym.crm.platform.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingValidator {

    public void validateForCreate(Training training) {
        Validation.requireNotNull(training, "Training");
        Validation.requireNotNull(training.getTrainee(), "Trainee");
        Validation.requireNotNull(training.getTrainer(), "Trainer");
        Validation.requireNotNull(training.getTrainingType(), "Training type");
        Validation.requireNotBlank(training.getTrainingName(), "Training name");
        Validation.requireNotNull(training.getTrainingDate(), "Training date");
        Validation.requireNotNull(training.getTrainingDuration(), "Training duration");

        if (training.getTrainingDuration() > 300) {
            throw new IllegalArgumentException("Training duration must be not greater than 300");
        }
    }
}