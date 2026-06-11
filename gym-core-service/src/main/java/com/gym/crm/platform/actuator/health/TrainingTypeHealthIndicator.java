package com.gym.crm.platform.actuator.health;

import com.gym.crm.platform.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private static final String REPOSITORY_DETAIL = "repository";

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        try {
            long count = trainingTypeRepository.count();

            if (count > 0) {
                return Health.up()
                        .withDetail(REPOSITORY_DETAIL, "Available")
                        .withDetail("trainingTypesCount", count)
                        .build();
            }

            return Health.down()
                    .withDetail(REPOSITORY_DETAIL, "Available")
                    .withDetail("trainingTypesCount", count)
                    .withDetail("reason", "No training types found")
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail(REPOSITORY_DETAIL, "Unavailable")
                    .build();
        }
    }
}