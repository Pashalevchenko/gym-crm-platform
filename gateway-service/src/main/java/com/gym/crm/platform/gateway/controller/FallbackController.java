package com.gym.crm.platform.gateway.controller;

import com.gym.crm.platform.gateway.dto.FallbackResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.net.ConnectException;
import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final String ERROR_MESSAGE = "Service temporarily unavailable";
    private static final String TIMEOUT_MESSAGE = "TimeOut: %s did not respond within 3s";
    private static final String CONNECTION_MESSAGE = "Connection error: Cannot connect to %s";

    @RequestMapping(value = "/{serviceName}", method = {GET, POST, PUT, PATCH, DELETE})
    public ResponseEntity<FallbackResponse> fallback(@PathVariable String serviceName, ServerWebExchange exchange) {
        Throwable exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        Set<URI> originalUris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);

        log.info("Circuit breaker fallback triggered for service={}, method={}, path={}, originalUris={}, reason={}",
                 serviceName,
                 exchange.getRequest().getMethod(),
                 exchange.getRequest().getPath(),
                 originalUris,
                 exception == null ? "unknown" : exception.getMessage());

        FallbackDetails details = resolveFallbackDetails(serviceName, exception);

        FallbackResponse response = new FallbackResponse(Instant.now(), details.status().value(), details.message(), serviceName);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    private FallbackDetails resolveFallbackDetails(String serviceName, Throwable exception) {
        if (hasCause(exception, TimeoutException.class)) {
            return new FallbackDetails(HttpStatus.GATEWAY_TIMEOUT, String.format(TIMEOUT_MESSAGE, serviceName));
        }

        if (hasCause(exception, ConnectException.class)) {
            return new FallbackDetails(HttpStatus.SERVICE_UNAVAILABLE, String.format(CONNECTION_MESSAGE, serviceName));
        }

        return new FallbackDetails(HttpStatus.SERVICE_UNAVAILABLE, ERROR_MESSAGE);
    }

    private boolean hasCause(Throwable exception, Class<? extends Throwable> expectedType) {
        Throwable current = exception;

        while (current != null) {
            if (expectedType.isInstance(current)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}