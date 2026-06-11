package com.gym.crm.platform.service.common;

import com.gym.crm.platform.exception.AuthenticationFailedException;
import com.gym.crm.platform.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_AUTHORIZATION_HEADER_MESSAGE = "Invalid authorization header";

    private final TokenBlacklistService service;

    public void logout(String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        service.blacklist(token);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationFailedException(INVALID_AUTHORIZATION_HEADER_MESSAGE);
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            throw new AuthenticationFailedException(INVALID_AUTHORIZATION_HEADER_MESSAGE);
        }

        return token;
    }
}
