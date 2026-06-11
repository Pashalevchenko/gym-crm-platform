package com.gym.crm.platform.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT authentication filter tests")
class JwtAuthenticationFilterTest {

    private static final String USERNAME = "test.user";
    private static final String TOKEN = "jwt-token";

    @Mock
    private JwtService jwtService;

    @Mock
    private GymUserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should set authentication when bearer token is valid")
    void doFilter_validBearerToken_shouldSetAuthentication() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(USERNAME)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(tokenBlacklistService.isBlacklisted(TOKEN)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(USERNAME);
        assertThat(authentication.isAuthenticated()).isTrue();
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService).extractUsername(TOKEN);
        verify(userDetailsService).loadUserByUsername(USERNAME);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header is missing")
    void doFilter_missingAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header is not bearer")
    void doFilter_notBearerAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc123");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when token is invalid")
    void doFilter_invalidToken_shouldNotSetAuthentication() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when token is blacklisted")
    void doFilter_blacklistedToken_shouldNotSetAuthentication() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);

        when(tokenBlacklistService.isBlacklisted(TOKEN)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(tokenBlacklistService).isBlacklisted(TOKEN);
        verify(jwtService, never()).isTokenValid(any());
        verify(jwtService, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }
}