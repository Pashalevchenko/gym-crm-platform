package com.gym.crm.application.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RestLoggingFilterTest {

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Test
    @DisplayName("Should continue filter chain and log completed REST call")
    void doFilterInternal_shouldContinueFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/v1/trainees/test.user");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(ContentCachingRequestWrapper.class), org.mockito.ArgumentMatchers.eq(response));
    }

    @Test
    @DisplayName("Should return empty request body when content is empty")
    void getRequestBody_whenContentIsEmpty_shouldReturnEmptyString() throws Exception {
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(new MockHttpServletRequest());

        String actual = invokeGetRequestBody(request);

        assertEquals("", actual);
    }

    @Test
    @DisplayName("Should mask password fields in request body")
    void maskSensitiveData_shouldMaskPasswordFields() throws Exception {
        String body = """
                {
                  "username": "test.user",
                  "password": "password123",
                  "oldPassword": "oldPassword123",
                  "newPassword": "newPassword123"
                }
                """;
        String expected = """
                {
                  "username": "test.user",
                  "password": "***",
                  "oldPassword": "***",
                  "newPassword": "***"
                }
                """;

        String actual = invokeMaskSensitiveData(body);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should return empty string when body is null")
    void maskSensitiveData_whenBodyIsNull_shouldReturnEmptyString() throws Exception {
        String actual = invokeMaskSensitiveData(null);

        assertEquals("", actual);
    }

    @Test
    @DisplayName("Should return empty string when body is blank")
    void maskSensitiveData_whenBodyIsBlank_shouldReturnEmptyString() throws Exception {
        String actual = invokeMaskSensitiveData("   ");

        assertEquals("", actual);
    }

    @Test
    @DisplayName("Should not throw when request has JSON body")
    void doFilterInternal_whenRequestHasJsonBody_shouldNotThrow() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/v1/auth/login");
        request.setContentType("application/json");
        request.setContent("""
                {
                  "username": "test.user",
                  "password": "password123"
                }
                """.getBytes(StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));
    }

    private String invokeGetRequestBody(ContentCachingRequestWrapper request) throws Exception {
        Method method = RestLoggingFilter.class.getDeclaredMethod("getRequestBody", ContentCachingRequestWrapper.class);
        method.setAccessible(true);

        return (String) method.invoke(filter, request);
    }

    private String invokeMaskSensitiveData(String body) throws Exception {
        Method method = RestLoggingFilter.class.getDeclaredMethod("maskSensitiveData", String.class);
        method.setAccessible(true);

        return (String) method.invoke(filter, body);
    }
}