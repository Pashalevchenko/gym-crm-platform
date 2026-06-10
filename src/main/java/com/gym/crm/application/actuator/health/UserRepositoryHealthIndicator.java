package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;

    @Override
    public Health health() {
        try {
            long usersCount = userRepository.count();

            return Health.up()
                    .withDetail("usersCount", usersCount)
                    .withDetail("repository", "Available")
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("repository", "Unavailable")
                    .build();
        }
    }
}