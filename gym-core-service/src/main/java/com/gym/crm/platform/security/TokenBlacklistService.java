package com.gym.crm.platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void blacklist(String token) {
        Date expirationDate = jwtService.extractExpiration(token);
        long remainingTimeInMillis = expirationDate.getTime() - System.currentTimeMillis();

        if (remainingTimeInMillis > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", remainingTimeInMillis, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}