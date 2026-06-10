package com.gym.crm.application.validation;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TraineeValidatorTest {

    private TraineeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TraineeValidator();
    }

    @Test
    @DisplayName("Should pass validation when trainee create request is valid")
    void validateForCreate_whenTraineeIsValid_shouldNotThrowException() {
        Trainee trainee = Trainee.builder()
                .user(generateUser("Ivan", "Ivanov"))
                .build();

        assertDoesNotThrow(() -> validator.validateForCreate(trainee));
    }

    @Test
    @DisplayName("Should throw exception when trainee create request is null")
    void validateForCreate_whenTraineeIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(null));
    }

    @Test
    @DisplayName("Should throw exception when trainee first name is blank")
    void validateForCreate_whenFirstNameIsBlank_shouldThrowException() {
        Trainee trainee = Trainee.builder()
                .user(generateUser(" ", "Ivanov"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(trainee));
    }

    @Test
    @DisplayName("Should throw exception when trainee last name is blank")
    void validateForCreate_whenLastNameIsBlank_shouldThrowException() {
        Trainee trainee = Trainee.builder()
                .user(generateUser("Ivan", " "))
                .build();

        assertThrows(IllegalArgumentException.class, () -> validator.validateForCreate(trainee));
    }

    private User generateUser(String firstName, String lastName){
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
