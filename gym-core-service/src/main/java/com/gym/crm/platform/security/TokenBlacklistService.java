package com.gym.crm.platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;
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
            redisTemplate.opsForValue().set(blacklistKey(token), "true", remainingTimeInMillis, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(blacklistKey(token));
    }

    private String blacklistKey(String token) {
        return BLACKLIST_PREFIX + hashToken(token);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Hashing is not available", exception);
        }
    }
}