package com.gym.crm.platform.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    private static final String TOKEN = "jwt-token";
    private static final String BLACKLIST_KEY = "blacklist:" + TOKEN;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistService service;

    @Test
    void blacklist_shouldMarkTokenAsBlacklisted_WhenTokenIsActive() {
        when(jwtService.extractExpiration(TOKEN)).thenReturn(new Date(System.currentTimeMillis() + 600000));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.blacklist(TOKEN);

        verify(valueOperations).set(eq(BLACKLIST_KEY), eq("true"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void blacklist_shouldNotWriteToRedis_WhenTokenIsAlreadyExpired() {
        when(jwtService.extractExpiration(TOKEN)).thenReturn(new Date(System.currentTimeMillis() - 300000));

        service.blacklist(TOKEN);

        verify(redisTemplate, never()).opsForValue();
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void isBlacklisted_shouldReturnTrue_WhenKeyExistsInRedis() {
        when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(true);

        boolean result = service.isBlacklisted(TOKEN);

        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(BLACKLIST_KEY);
    }

    @Test
    void isBlacklisted_shouldReturnFalseForUnknownToken() {
        when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(false);

        boolean result = service.isBlacklisted(TOKEN);

        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(BLACKLIST_KEY);
    }
}