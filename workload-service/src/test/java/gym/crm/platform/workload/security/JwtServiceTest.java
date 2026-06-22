package gym.crm.platform.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JWT service tests")
class JwtServiceTest {

    private static final String SECRET = "test-secret-key-test-secret-key-123456";
    private static final long EXPIRATION_MS = 3600000L;
    private static final String USERNAME = "test.user";

    private final JwtService jwtService = new JwtService(SECRET);

    @Test
    @DisplayName("Should return true when token is valid")
    void isTokenValid_shouldReturnTrueWhenTokenIsValid() {
        String token = generateToken(USERNAME, EXPIRATION_MS);

        boolean actual = jwtService.isTokenValid(token);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_shouldReturnUsername() {
        String token = generateToken(USERNAME, EXPIRATION_MS);

        String actual = jwtService.extractUsername(token);

        assertThat(actual).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("Should return false when token is invalid")
    void isTokenValid_invalidToken_shouldReturnFalse() {
        boolean actual = jwtService.isTokenValid("invalid.jwt.token");

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Should return false when token is expired")
    void isTokenValid_expiredToken_shouldReturnFalse() {
        String token = generateToken(USERNAME, -1000L);

        boolean actual = jwtService.isTokenValid(token);

        assertThat(actual).isFalse();
    }

    private String generateToken(String username, long expirationOffsetMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationOffsetMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}