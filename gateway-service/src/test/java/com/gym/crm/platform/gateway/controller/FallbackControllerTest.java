package com.gym.crm.platform.gateway.controller;

import com.gym.crm.platform.gateway.dto.FallbackResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FallbackController controller;

    @Test
    void fallback_whenCalledWithGet_shouldReturnServiceUnavailable() {
        webTestClient.get()
                .uri("/fallback/gym-core-service")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
                .jsonPath("$.error").isEqualTo("Service temporarily unavailable")
                .jsonPath("$.service").isEqualTo("gym-core-service")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void gymCoreFallback_shouldReturnServiceUnavailable() {
        webTestClient.put()
                .uri("/fallback/workload-service")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
                .jsonPath("$.error").isEqualTo("Service temporarily unavailable")
                .jsonPath("$.service").isEqualTo("workload-service")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void fallback_whenConnectExceptionOccurs_shouldReturnServiceUnavailable() {
        MockServerWebExchange exchange = exchangeWithException(new RuntimeException(new ConnectException("Connection refused")));

        ResponseEntity<FallbackResponse> actual = controller.fallback("gym-core-service", exchange);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().status()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(actual.getBody().error()).isEqualTo("Connection error: Cannot connect to gym-core-service");
        assertThat(actual.getBody().service()).isEqualTo("gym-core-service");
    }

    private MockServerWebExchange exchangeWithException(Throwable exception) {
        MockServerHttpRequest request = MockServerHttpRequest.get("/fallback/workload-service").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getAttributes().put(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, exception);

        return exchange;
    }
}