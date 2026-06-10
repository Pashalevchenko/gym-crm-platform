package com.gym.crm.application.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }
}