package com.gym.crm.application.actuator.metrics;

import com.gym.crm.application.repository.TraineeRepository;
import com.gym.crm.application.repository.TrainerRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Gym metrics service tests")
class GymMetricsServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    private SimpleMeterRegistry meterRegistry;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry, traineeRepository, trainerRepository);
    }

    @Test
    @DisplayName("Should increment trainee created counter")
    void incrementTraineeCreated_shouldIncreaseCounter() {
        metricsService.incrementTraineeCreated();

        double actual = meterRegistry.counter("gym.trainees.created").count();

        assertThat(actual).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment trainer created counter")
    void incrementTrainerCreated_shouldIncreaseCounter() {
        metricsService.incrementTrainerCreated();

        double actual = meterRegistry.counter("gym.trainers.created").count();

        assertThat(actual).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should increment training created counter")
    void incrementTrainingCreated_shouldIncreaseCounter() {
        metricsService.incrementTrainingCreated();

        double actual = meterRegistry.counter("gym.trainings.created").count();

        assertThat(actual).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should return active trainees gauge value")
    void activeTraineesGauge_shouldReturnRepositoryCount() {
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(5L);

        double actual = meterRegistry.get("gym.trainees.active").gauge().value();

        assertThat(actual).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should return active trainers gauge value")
    void activeTrainersGauge_shouldReturnRepositoryCount() {
        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(3L);

        double actual = meterRegistry.get("gym.trainers.active").gauge().value();

        assertThat(actual).isEqualTo(3.0);
    }
}