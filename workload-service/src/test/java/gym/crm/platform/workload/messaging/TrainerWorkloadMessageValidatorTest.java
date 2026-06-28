package gym.crm.platform.workload.messaging;

import gym.crm.platform.workload.exception.InvalidWorkloadMessageException;
import gym.crm.platform.workload.model.ActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Trainer workload message validator tests")
class TrainerWorkloadMessageValidatorTest {

    private final TrainerWorkloadMessageValidator validator = new TrainerWorkloadMessageValidator();

    @Test
    @DisplayName("Should accept valid message")
    void validate_whenMessageIsValid_shouldNotThrow() {
        TrainerWorkloadMessage message = createValidMessage();

        assertDoesNotThrow(() -> validator.validate(message));
    }

    @Test
    @DisplayName("Should reject empty message")
    void validate_whenMessageIsEmpty_shouldThrowException() {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage(null, null, null, null, null, null, null);

        assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(InvalidWorkloadMessageException.class)
                .hasMessage("Required workload message field is missing: trainerUsername");
    }

    @Test
    @DisplayName("Should reject message with missing action type")
    void validate_whenActionTypeIsMissing_shouldThrowException() {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, null);

        assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(InvalidWorkloadMessageException.class)
                .hasMessage("Required workload message field is missing: actionType");
    }

    @Test
    @DisplayName("Should reject message with non-positive duration")
    void validate_whenTrainingDurationIsNotPositive_shouldThrowException() {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 0, ActionType.ADD);

        assertThatThrownBy(() -> validator.validate(message))
                .isInstanceOf(InvalidWorkloadMessageException.class)
                .hasMessage("Training duration must be positive");
    }

    private TrainerWorkloadMessage createValidMessage() {
        return new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}