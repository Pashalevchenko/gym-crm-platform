package com.gym.crm.application.actuator.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Database health indicator tests")
class DatabaseHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DatabaseHealthIndicator databaseHealthIndicator;

    @Test
    @DisplayName("Should return UP when database is available")
    void health_databaseAvailable_shouldReturnUp() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        Health actual = databaseHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        assertThat(actual.getDetails()).containsEntry("database", "Available").containsEntry("validationQuery", "SELECT 1");
        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
    }

    @Test
    @DisplayName("Should return DOWN when database returns unexpected result")
    void health_unexpectedValidationResult_shouldReturnDown() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(0);

        Health actual = databaseHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("database", "Unexpected validation result").containsEntry("validationResult", 0);
        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
    }

    @Test
    @DisplayName("Should return DOWN when database is unavailable")
    void health_databaseUnavailable_shouldReturnDown() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("Database unavailable"));

        Health actual = databaseHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("database", "Unavailable");
        verify(jdbcTemplate).queryForObject("SELECT 1", Integer.class);
    }
}