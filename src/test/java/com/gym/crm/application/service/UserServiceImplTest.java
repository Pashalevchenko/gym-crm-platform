package com.gym.crm.application.service;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.repository.UserRepository;
import com.gym.crm.application.service.common.AuthenticationService;
import com.gym.crm.application.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authentication;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void changePasswordShouldUpdatePasswordWhenOldPasswordIsCorrect() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("ricardo.milos")
                .oldPassword("old-password")
                .newPassword("new-password");
        User existingUser = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-old-password")
                .isActive(true)
                .build();

        when(repository.findByUsername("ricardo.milos")).thenReturn(Optional.of(existingUser));
        when(authentication.encodePassword("new-password")).thenReturn("encoded-new-password");

        service.changePassword(request);

        verify(authentication).verifyPassword("old-password", "encoded-old-password",
                "The provided old password does not match the current password");
        verify(authentication).encodePassword("new-password");
        verify(repository).save(argThat(actual ->
                actual.getUsername().equals("ricardo.milos") &&
                        actual.getPassword().equals("encoded-new-password") &&
                        actual.getId().equals(1L)));
    }

    @Test
    void changePasswordShouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("unknown.user")
                .oldPassword("old-password")
                .newPassword("new-password");

        when(repository.findByUsername("unknown.user"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePasswordShouldThrowIllegalArgumentExceptionWhenOldPasswordIsInvalid() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("ricardo.milos")
                .oldPassword("wrong-password")
                .newPassword("new-password");
        User existingUser = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-old-password")
                .isActive(true)
                .build();

        when(repository.findByUsername("ricardo.milos")).thenReturn(Optional.of(existingUser));

        doThrow(new IllegalArgumentException("The provided old password does not match the current password"))
                .when(authentication)
                .verifyPassword("wrong-password", "encoded-old-password",
                        "The provided old password does not match the current password");
        assertThatThrownBy(() -> service.changePassword(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The provided old password does not match the current password");
        verify(authentication, never()).encodePassword(anyString());
    }

    @Test
    void findByUsernameShouldReturnUserWhenUserExists() {
        User user = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-password")
                .isActive(true)
                .build();

        when(repository.findByUsername("ricardo.milos")).thenReturn(Optional.of(user));

        User result = service.findByUsername("ricardo.milos");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findByUsernameShouldThrowNoSuchElementExceptionWhenUserDoesNotExist() {
        when(repository.findByUsername("unknown.user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUsername("unknown.user"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }
}