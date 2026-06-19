package com.gym.crm.platform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/gym-core-service")
    public ResponseEntity<Map<String, Object>> gymCoreFallback() {
        return fallbackResponse("gym-core-service");
    }

    @GetMapping("/fallback/workload-service")
    public ResponseEntity<Map<String, Object>> workloadFallback() {
        return fallbackResponse("workload-service");
    }

    private ResponseEntity<Map<String, Object>> fallbackResponse(String serviceName) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("timestamp", Instant.now().toString(),
                             "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                             "error", "Service temporarily unavailable",
                             "service", serviceName));
    }
}