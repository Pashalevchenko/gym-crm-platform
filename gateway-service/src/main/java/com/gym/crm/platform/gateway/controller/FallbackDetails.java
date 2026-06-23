package com.gym.crm.platform.gateway.controller;

import org.springframework.http.HttpStatus;

record FallbackDetails(HttpStatus status, String message) {
}
