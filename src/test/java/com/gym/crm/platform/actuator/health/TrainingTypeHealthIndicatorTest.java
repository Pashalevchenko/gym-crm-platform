package com.gym.crm.platform.actuator.health;

import com.gym.crm.platform.repository.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Training type health indicator tests")
class TrainingTypeHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeHealthIndicator trainingTypeHealthIndicator;

    @Test
    @DisplayName("Should return UP when training type repository is available")
    void health_repositoryAvailable_shouldReturnUp() {
        when(trainingTypeRepository.count()).thenReturn(2L);

        Health actual = trainingTypeHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        assertThat(actual.getDetails()).containsEntry("repository", "Available").containsEntry("trainingTypesCount", 2L);
        verify(trainingTypeRepository).count();
    }

    @Test
    @DisplayName("Should return DOWN when training type repository is unavailable")
    void health_repositoryUnavailable_shouldReturnDown() {
        when(trainingTypeRepository.count()).thenThrow(new RuntimeException("Database unavailable"));

        Health actual = trainingTypeHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("repository", "Unavailable");
        verify(trainingTypeRepository).count();
    }
}