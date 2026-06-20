package com.gym.crm.platform.gateway.dto;

import java.time.Instant;

public record FallbackResponse(Instant timestamp, int status, String error, String service) {
}