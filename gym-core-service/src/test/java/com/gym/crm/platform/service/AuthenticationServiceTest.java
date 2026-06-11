package com.gym.crm.platform.service;

import com.gym.crm.platform.entity.User;
import com.gym.crm.platform.exception.AuthenticationFailedException;
import com.gym.crm.platform.repository.UserRepository;
import com.gym.crm.platform.security.JwtService;
import com.gym.crm.platform.security.LoginAttemptService;
import com.gym.crm.platform.service.common.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final String USERNAME = "borys.burpee";
    private final String PASSWORD = "correct_password";

    @Mock
    private UserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private LoginAttemptService attemptService;

    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("Should return token when credentials are valid")
    void authenticate_success() {
        String rawPasswordInput = "rawPassword123";
        String encodedPasswordInDb = "hashedPassword789";
        String token = "jwt-token";
        User user = User.builder()
                .username(USERNAME)
                .password(encodedPasswordInDb)
                .build();

        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPasswordInput, encodedPasswordInDb)).thenReturn(true);
        when(jwtService.generateToken(USERNAME)).thenReturn(token);

        String actual = authService.authenticate(USERNAME, rawPasswordInput);

        assertEquals(token, actual);
        verify(repository).findByUsername(USERNAME);
        verify(passwordEncoder).matches(rawPasswordInput, encodedPasswordInDb);
        verify(jwtService).generateToken(USERNAME);
    }

    @Test
    @DisplayName("Should throw RuntimeException when user is not found in database")
    void authenticate_userNotFound_throwsException() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class,
                () -> authService.authenticate(USERNAME, PASSWORD));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(repository).findByUsername(USERNAME);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when password does not match")
    void authenticate_invalidPassword_throwsException() {
        String rawPasswordInput = "wrong_password";
        String encodedPasswordInDb = "hashed_password_in_db";

        User user = User.builder()
                .username(USERNAME)
                .password(encodedPasswordInDb)
                .build();

        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPasswordInput, encodedPasswordInDb)).thenReturn(false);
        when(passwordEncoder.matches(rawPasswordInput, encodedPasswordInDb)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(USERNAME, "wrong_password"));
        assertEquals("Invalid username or password", exception.getMessage());
    }
}