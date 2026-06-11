package com.gym.crm.platform.service;

import com.gym.crm.platform.exception.AuthenticationFailedException;
import com.gym.crm.platform.security.TokenBlacklistService;
import com.gym.crm.platform.service.common.LogoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    private static final String TOKEN = "jwt-token";
    private static final String AUTHORIZATION_HEADER = "Bearer " + TOKEN;
    private static final String INVALID_AUTHORIZATION_HEADER_MESSAGE = "Invalid authorization header";

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private LogoutService service;

    @Test
    @DisplayName("Should blacklist token when authorization header is valid")
    void logout_validAuthorizationHeader_shouldBlacklistToken() {
        assertDoesNotThrow(() -> service.logout(AUTHORIZATION_HEADER));

        verify(tokenBlacklistService).blacklist(TOKEN);
    }

    @Test
    @DisplayName("Should throw exception when authorization header is null")
    void logout_nullAuthorizationHeader_shouldThrowException() {
        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, () -> service.logout(null));

        assertEquals(INVALID_AUTHORIZATION_HEADER_MESSAGE, exception.getMessage());
        verify(tokenBlacklistService, never()).blacklist(TOKEN);
    }

    @Test
    @DisplayName("Should throw exception when authorization header does not start with Bearer prefix")
    void logout_invalidAuthorizationHeader_shouldThrowException() {
        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, () -> service.logout(TOKEN));

        assertEquals(INVALID_AUTHORIZATION_HEADER_MESSAGE, exception.getMessage());
        verify(tokenBlacklistService, never()).blacklist(TOKEN);
    }
}