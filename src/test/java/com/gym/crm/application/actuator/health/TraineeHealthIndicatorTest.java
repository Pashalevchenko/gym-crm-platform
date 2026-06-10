package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TraineeRepository;
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
@DisplayName("Trainee health indicator tests")
class TraineeHealthIndicatorTest {

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TraineeHealthIndicator traineeHealthIndicator;

    @Test
    @DisplayName("Should return UP when trainee repository is available")
    void health_repositoryAvailable_shouldReturnUp() {
        when(traineeRepository.count()).thenReturn(2L);

        Health actual = traineeHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        assertThat(actual.getDetails()).containsEntry("repository", "Available").containsEntry("traineesCount", 2L);
        verify(traineeRepository).count();
    }

    @Test
    @DisplayName("Should return DOWN when trainee repository is unavailable")
    void health_repositoryUnavailable_shouldReturnDown() {
        when(traineeRepository.count()).thenThrow(new RuntimeException("Database unavailable"));

        Health actual = traineeHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("repository", "Unavailable");
        verify(traineeRepository).count();
    }
}