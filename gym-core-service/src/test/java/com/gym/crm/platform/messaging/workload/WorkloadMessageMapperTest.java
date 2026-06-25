package com.gym.crm.platform.messaging.workload;

import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Workload message mapper tests")
class WorkloadMessageMapperTest {

    private static final String USERNAME = "trainer.user";
    private static final String FIRST_NAME = "Trainer";
    private static final String LAST_NAME = "User";

    private final WorkloadMessageMapper mapper = new WorkloadMessageMapper();

    @Test
    @DisplayName("Should map training to trainer workload message")
    void toMessage_shouldMapTrainingToTrainerWorkloadMessage() {
        User user = User.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .user(user)
                .build();
        Training training = Training.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.of(2026, Month.JUNE, 10))
                .trainingDuration(60)
                .build();

        TrainerWorkloadMessage actual = mapper.toMessage(training, ActionType.ADD);

        assertThat(actual.trainerUsername()).isEqualTo(USERNAME);
        assertThat(actual.trainerFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.trainerLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.isActive()).isTrue();
        assertThat(actual.trainingDate()).isEqualTo(LocalDate.of(2026, Month.JUNE, 10));
        assertThat(actual.trainingDuration()).isEqualTo(60);
        assertThat(actual.actionType()).isEqualTo(ActionType.ADD);
    }
}
