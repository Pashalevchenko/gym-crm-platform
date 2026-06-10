package com.gym.crm.platform.validation;

import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainerValidatorTest {

    private TrainerValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TrainerValidator();
    }

    @Test
    @DisplayName("Should pass validation when trainer create request is valid")
    void validateForCreate_whenTrainerIsValid_shouldNotThrowException() {
        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Yoga")
                .build();
        Trainer trainer = Trainer.builder()
                .user(generateUser("Ivan", "Ivanov"))
                .specialization(trainingType)
                .build();

        assertDoesNotThrow(() -> validator.validateForCreate(trainer));
    }

    @Test
    @DisplayName("Should throw exception when trainer create request is null")
    void validateForCreate_whenTrainerIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(null));
    }

    @Test
    @DisplayName("Should throw exception when trainer first name is blank")
    void validateForCreate_whenFirstNameIsBlank_shouldThrowException() {
        Trainer trainer = Trainer.builder()
                .user(generateUser(" ", "Ivanov"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(trainer));
    }

    @Test
    @DisplayName("Should throw exception when trainer last name is blank")
    void validateForCreate_whenLastNameIsBlank_shouldThrowException() {
        Trainer trainer = Trainer.builder()
                .user(generateUser("Ivan", " "))
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(trainer));
    }

    private User generateUser(String firstName, String lastName){
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
