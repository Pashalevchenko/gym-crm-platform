package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TrainerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Trainer health indicator tests")
class TrainerHealthIndicatorTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerHealthIndicator trainerHealthIndicator;

    @Test
    @DisplayName("Should return UP when trainer repository is available")
    void health_repositoryAvailable_shouldReturnUp() {
        when(trainerRepository.count()).thenReturn(3L);

        Health actual = trainerHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        assertThat(actual.getDetails()).containsEntry("repository", "Available").containsEntry("trainersCount", 3L);
        verify(trainerRepository).count();
    }

    @Test
    @DisplayName("Should return DOWN when trainer repository is unavailable")
    void health_repositoryUnavailable_shouldReturnDown() {
        when(trainerRepository.count()).thenThrow(new RuntimeException("Database unavailable"));

        Health actual = trainerHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("repository", "Unavailable");
        verify(trainerRepository).count();
    }
}