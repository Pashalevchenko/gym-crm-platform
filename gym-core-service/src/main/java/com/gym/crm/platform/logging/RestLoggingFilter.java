package com.gym.crm.platform.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            String requestBody = maskSensitiveData(getRequestBody(wrappedRequest));

            log.info("REST call completed. transactionId={}, method={}, endpoint={}, query={}, requestBody={}, responseStatus={}",
                    MDC.get("transactionId"),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    requestBody,
                    response.getStatus());
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return new String(content, StandardCharsets.UTF_8);
    }

    private String maskSensitiveData(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        return body
                .replaceAll("(\"password\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3")
                .replaceAll("(\"oldPassword\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3")
                .replaceAll("(\"newPassword\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");
    }
}