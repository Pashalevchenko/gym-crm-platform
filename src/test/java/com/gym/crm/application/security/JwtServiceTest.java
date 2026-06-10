package com.gym.crm.application.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JWT service tests")
class JwtServiceTest {

    private static final String SECRET = "test-secret-key-test-secret-key-123456";
    private static final long EXPIRATION_MS = 3600000L;
    private static final String USERNAME = "test.user";

    private final JwtService jwtService = new JwtService(SECRET, EXPIRATION_MS);

    @Test
    @DisplayName("Should generate valid token")
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken(USERNAME);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_shouldReturnUsername() {
        String token = jwtService.generateToken(USERNAME);

        String actual = jwtService.extractUsername(token);

        assertThat(actual).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("Should return false when token is invalid")
    void isTokenValid_invalidToken_shouldReturnFalse() {
        boolean actual = jwtService.isTokenValid("invalid.jwt.token");

        assertThat(actual).isFalse();
    }
}