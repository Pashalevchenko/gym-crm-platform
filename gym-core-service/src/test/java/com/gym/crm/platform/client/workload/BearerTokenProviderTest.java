package com.gym.crm.platform.client.workload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bearer token provider tests")
class BearerTokenProviderTest {

    private static final String TOKEN = "jwt-token";

    private final BearerTokenProvider provider = new BearerTokenProvider();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should return token from bearer authorization header")
    void getToken_whenBearerHeaderExists_shouldReturnToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Optional<String> expected = Optional.of(TOKEN);

        Optional<String> actual = provider.getToken();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return empty when authorization header is missing")
    void getToken_whenAuthorizationHeaderIsMissing_shouldReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Optional<String> expected = Optional.empty();

        Optional<String> actual = provider.getToken();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return empty when authorization header is not bearer")
    void getToken_whenAuthorizationHeaderIsNotBearer_shouldReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Optional<String> expected = Optional.empty();

        Optional<String> actual = provider.getToken();

        assertThat(actual).isEqualTo(expected);
    }
}