package com.gym.crm.application.validation;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainingValidatorTest {

    private TrainingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TrainingValidator();
    }

    @Test
    @DisplayName("Should pass validation when training create request is valid")
    void validateForCreate_whenTrainingIsValid_shouldNotThrowException() {
        Training training = Training.builder()
                .trainee(Trainee.builder().id(1L).build())
                .trainer(Trainer.builder().id(2L).build())
                .trainingType(TrainingType.builder().id(3L).build())
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        assertDoesNotThrow(() -> validator.validateForCreate(training));
    }

    @Test
    @DisplayName("Should throw exception when training create request is null")
    void validateForCreate_whenTrainingIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(null));
    }

    @Test
    @DisplayName("Should throw exception when training name is blank")
    void validateForCreate_whenTrainingNameIsBlank_shouldThrowException() {
        Training training = Training.builder()
                .trainee(Trainee.builder().build())
                .trainer(Trainer.builder().build())
                .trainingType(TrainingType.builder().build())
                .trainingName(" ")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(training));
    }

    @Test
    @DisplayName("Should throw exception when training duration is zero or negative")
    void validateForCreate_whenDurationIsInvalid_shouldThrowException() {
        Training training = Training.builder()
                .trainee(Trainee.builder().build())
                .trainer(Trainer.builder().build())
                .trainingType(TrainingType.builder().build())
                .trainingName("Boxing")
                .trainingDate(LocalDate.now())
                .trainingDuration(9999)
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(training));
    }

    @Test
    @DisplayName("Should throw exception when mandatory field like Training Date is missing")
    void validateForCreate_whenDateIsNull_shouldThrowException() {
        Training training = Training.builder()
                .trainee(Trainee.builder().build())
                .trainer(Trainer.builder().build())
                .trainingType(TrainingType.builder().build())
                .trainingName("Cardio")
                .trainingDate(null)
                .trainingDuration(45)
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(training));
    }
}