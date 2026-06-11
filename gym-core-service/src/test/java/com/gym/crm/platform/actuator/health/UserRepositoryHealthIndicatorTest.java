package com.gym.crm.platform.actuator.health;

import com.gym.crm.platform.repository.UserRepository;
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
@DisplayName("User repository health indicator tests")
class UserRepositoryHealthIndicatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryHealthIndicator userRepositoryHealthIndicator;

    @Test
    @DisplayName("Should return UP when user repository is available")
    void health_repositoryAvailable_shouldReturnUp() {
        when(userRepository.count()).thenReturn(4L);

        Health actual = userRepositoryHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        assertThat(actual.getDetails()).containsEntry("repository", "Available").containsEntry("usersCount", 4L);
        verify(userRepository).count();
    }

    @Test
    @DisplayName("Should return DOWN when user repository is unavailable")
    void health_repositoryUnavailable_shouldReturnDown() {
        when(userRepository.count()).thenThrow(new RuntimeException("Database unavailable"));

        Health actual = userRepositoryHealthIndicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        assertThat(actual.getDetails()).containsEntry("repository", "Unavailable");
        verify(userRepository).count();
    }
}