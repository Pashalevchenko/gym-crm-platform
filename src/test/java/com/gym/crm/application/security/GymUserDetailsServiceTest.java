package com.gym.crm.application.security;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Gym user details service tests")
class GymUserDetailsServiceTest {

    private static final String USERNAME = "test.user";
    private static final String PASSWORD = "encoded-password";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GymUserDetailsService service;

    @Test
    @DisplayName("Should load user details when user exists")
    void loadUserByUsername_userExists_shouldReturnUserDetails() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(buildUser(true)));

        UserDetails actual = service.loadUserByUsername(USERNAME);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getPassword()).isEqualTo(PASSWORD);
        assertThat(actual.isEnabled()).isTrue();
        assertThat(actual.getAuthorities()).isEmpty();
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return disabled user details when user is inactive")
    void loadUserByUsername_inactiveUser_shouldReturnDisabledUserDetails() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(buildUser(false)));

        UserDetails actual = service.loadUserByUsername(USERNAME);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getPassword()).isEqualTo(PASSWORD);
        assertThat(actual.isEnabled()).isFalse();
        assertThat(actual.getAuthorities()).isEmpty();
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void loadUserByUsername_userNotFound_shouldThrowException() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername(USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username test.user not found");
        verify(userRepository).findByUsername(USERNAME);
    }

    private User buildUser(boolean active) {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(active)
                .build();
    }
}