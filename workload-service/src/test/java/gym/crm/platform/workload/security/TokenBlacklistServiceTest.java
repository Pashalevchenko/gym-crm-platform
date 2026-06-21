package gym.crm.platform.workload.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    private static final String TOKEN = "jwt-token";
    private static final String BLACKLIST_KEY = "blacklist:" + TOKEN;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private TokenBlacklistService service;

    @Test
    void isBlacklisted_shouldReturnTrue_WhenKeyExistsInRedis() {
        when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(true);

        boolean actual = service.isBlacklisted(TOKEN);

        assertThat(actual).isTrue();
        verify(redisTemplate).hasKey(BLACKLIST_KEY);
    }

    @Test
    void isBlacklisted_shouldReturnFalseForUnknownToken() {
        when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(false);

        boolean actual = service.isBlacklisted(TOKEN);

        assertThat(actual).isFalse();
        verify(redisTemplate).hasKey(BLACKLIST_KEY);
    }
}