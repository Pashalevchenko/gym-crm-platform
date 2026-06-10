package com.gym.crm.platform.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final String DATABASE_DETAIL = "database";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (Integer.valueOf(1).equals(result)) {
                return Health.up()
                        .withDetail(DATABASE_DETAIL, "Available")
                        .withDetail("validationQuery", "SELECT 1")
                        .build();
            }

            return Health.down()
                    .withDetail(DATABASE_DETAIL, "Unexpected validation result")
                    .withDetail("validationResult", result)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail(DATABASE_DETAIL, "Unavailable")
                    .build();
        }
    }
}