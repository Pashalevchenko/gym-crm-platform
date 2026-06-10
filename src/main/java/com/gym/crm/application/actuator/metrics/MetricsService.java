package com.gym.crm.application.actuator.metrics;

import com.gym.crm.application.repository.TraineeRepository;
import com.gym.crm.application.repository.TrainerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter traineeCreatedCounter;
    private final Counter trainerCreatedCounter;
    private final Counter trainingCreatedCounter;

    public MetricsService(MeterRegistry meterRegistry, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeCreatedCounter = Counter.builder("gym.trainees.created")
                .description("Total number of created trainees")
                .register(meterRegistry);

        this.trainerCreatedCounter = Counter.builder("gym.trainers.created")
                .description("Total number of created trainers")
                .register(meterRegistry);

        this.trainingCreatedCounter = Counter.builder("gym.trainings.created")
                .description("Total number of created trainings")
                .register(meterRegistry);

        Gauge.builder("gym.trainees.active", traineeRepository, TraineeRepository::countByUserIsActiveTrue)
                .description("Current number of active trainees")
                .register(meterRegistry);

        Gauge.builder("gym.trainers.active", trainerRepository, TrainerRepository::countByUserIsActiveTrue)
                .description("Current number of active trainers")
                .register(meterRegistry);
    }

    public void incrementTraineeCreated() {
        traineeCreatedCounter.increment();
    }

    public void incrementTrainerCreated() {
        trainerCreatedCounter.increment();
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
    }
}