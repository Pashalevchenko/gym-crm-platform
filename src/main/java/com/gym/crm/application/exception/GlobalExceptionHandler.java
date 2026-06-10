package com.gym.crm.application.exception;

import com.gym.crm.application.openapi.ErrorResponse;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.gym.crm.application.exception.ApiErrorCode.AUTHENTICATION_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.AUTHORIZATION_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.DATABASE_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.NOT_FOUND_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.SERVICE_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.USER_BLOCKED_ERROR;
import static com.gym.crm.application.exception.ApiErrorCode.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        String message = buildMessage(VALIDATION_ERROR, details);

        log.warn("Method argument validation failed: {}", message);
        return buildResponse(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        String message = buildMessage(VALIDATION_ERROR, exception.getMessage());

        log.warn("Validation failed: {}", message);
        return buildResponse(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        String message = buildMessage(VALIDATION_ERROR, exception.getMessage());

        log.warn("Illegal argument exception occurred: {}", message);
        return buildResponse(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException exception) {
        String message = buildMessage(NOT_FOUND_ERROR, exception.getMessage());

        log.warn("Requested resource was not found: {}", exception.getMessage(), exception);
        return buildResponse(NOT_FOUND_ERROR, message);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(AuthorizationException exception) {
        log.warn("Authorization fail: {}", exception.getMessage(), exception);

        return buildResponse(AUTHORIZATION_ERROR, AUTHORIZATION_ERROR.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException exception) {
        String message = buildMessage(DATABASE_ERROR, exception.getMessage());

        log.error("Spring data access fail: {}", exception.getMessage(), exception);
        return buildResponse(DATABASE_ERROR, message);
    }

    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<ErrorResponse> handleHibernateException(HibernateException exception) {
        String message = buildMessage(DATABASE_ERROR, exception.getMessage());

        log.error("Hibernate persistence fail: {}", exception.getMessage(), exception);
        return buildResponse(DATABASE_ERROR, message);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handlePersistenceException(PersistenceException exception) {
        String message = buildMessage(DATABASE_ERROR, exception.getMessage());

        log.error("JPA persistence fail: {}", exception.getMessage(), exception);
        return buildResponse(DATABASE_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Unexpected application error occurred", exception);

        return buildResponse(SERVICE_ERROR, SERVICE_ERROR.getMessage());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationFailedException exception) {
        log.warn("Authentication fail: {}", exception.getMessage(), exception);

        return buildResponse(AUTHENTICATION_ERROR, AUTHENTICATION_ERROR.getMessage());
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlockedException(UserBlockedException exception) {
        log.warn("User blocked: {}", exception.getMessage(), exception);

        return buildResponse(USER_BLOCKED_ERROR, exception.getMessage());
    }

    private String buildMessage(ApiErrorCode apiErrorCode, String exceptionMessage) {
        return String.format("%s: %s", apiErrorCode.getMessage(), exceptionMessage);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiErrorCode errorCode, String message) {
        ErrorResponse response = new ErrorResponse()
                .errorCode(errorCode.getCode())
                .errorMessage(message);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}