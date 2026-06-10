package com.gym.crm.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final GymUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (shouldAuthenticate(token)) {
            authenticateRequest(token, request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateRequest(String token, HttpServletRequest request) {
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = buildAuthentication(userDetails, request);

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean shouldAuthenticate(String token) {
        boolean isNotAuthenticatedYet = SecurityContextHolder.getContext().getAuthentication() == null;

        return isNotAuthenticatedYet && !tokenBlacklistService.isBlacklisted(token) && jwtService.isTokenValid(token);
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }
}