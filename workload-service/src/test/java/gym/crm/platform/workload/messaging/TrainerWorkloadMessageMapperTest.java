package gym.crm.platform.workload.messaging;

import gym.crm.platform.workload.model.ActionType;
import gym.crm.platform.workload.openapi.TrainerWorkloadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Trainer workload message mapper tests")
class TrainerWorkloadMessageMapperTest {

    private static final String USERNAME = "trainer.user";
    private static final String FIRST_NAME = "Trainer";
    private static final String LAST_NAME = "User";

    private final TrainerWorkloadMessageMapper mapper = new TrainerWorkloadMessageMapper();

    @Test
    @DisplayName("Should map trainer workload message to request")
    void toRequest_shouldMapTrainerWorkloadMessageToRequest() {
        TrainerWorkloadMessage message = createMessage();

        TrainerWorkloadRequest actual = mapper.toRequest(message);

        assertThat(actual.getTrainerUsername()).isEqualTo(USERNAME);
        assertThat(actual.getTrainerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getTrainerLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getIsActive()).isTrue();
        assertThat(actual.getTrainingDate()).isEqualTo(LocalDate.of(2026, Month.JUNE, 10));
        assertThat(actual.getTrainingDuration()).isEqualTo(60);
        assertThat(actual.getActionType().getValue()).isEqualTo(ActionType.ADD.name());
    }

    private TrainerWorkloadMessage createMessage() {
        return new TrainerWorkloadMessage(USERNAME, FIRST_NAME, LAST_NAME, true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}
