package gym.crm.platform.workload.exception;

import gym.crm.platform.workload.openapi.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static gym.crm.platform.workload.exception.ApiErrorCode.DATABASE_ERROR;
import static gym.crm.platform.workload.exception.ApiErrorCode.NOT_FOUND_ERROR;
import static gym.crm.platform.workload.exception.ApiErrorCode.SERVICE_ERROR;
import static gym.crm.platform.workload.exception.ApiErrorCode.VALIDATION_ERROR;
import static gym.crm.platform.workload.exception.ApiErrorCode.MISSING_REQUEST_DATA_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = validationMessage(exception);

        log.warn("Method argument validation failed: {}", message);
        return error(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        String message = messageWithDetails(VALIDATION_ERROR, exception.getMessage());

        log.warn("Validation failed: {}", message);
        return error(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        String message = messageWithDetails(VALIDATION_ERROR, exception.getMessage());

        log.warn("Illegal argument exception occurred: {}", message);
        return error(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException exception) {
        String message = messageWithDetails(NOT_FOUND_ERROR, exception.getMessage());

        log.warn("Requested resource was not found: {}", exception.getMessage(), exception);
        return error(NOT_FOUND_ERROR, message);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException exception) {
        log.warn("Required request data is missing", exception);

        return error(VALIDATION_ERROR, MISSING_REQUEST_DATA_ERROR.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException exception) {
        String message = messageWithDetails(DATABASE_ERROR, exception.getMessage());

        log.error("Spring data access fail: {}", exception.getMessage(), exception);
        return error(DATABASE_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Unexpected application error occurred", exception);

        return error(SERVICE_ERROR, SERVICE_ERROR.getMessage());
    }

    private String validationMessage(MethodArgumentNotValidException exception) {
        String fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return messageWithDetails(VALIDATION_ERROR, fields);
    }

    private String messageWithDetails(ApiErrorCode apiErrorCode, String details) {
        return String.join(": ", apiErrorCode.getMessage(), details);
    }

    private ResponseEntity<ErrorResponse> error(ApiErrorCode apiErrorCode, String message) {
        return ResponseEntity.status(apiErrorCode.getStatus())
                .body(new ErrorResponse()
                        .errorCode(apiErrorCode.getCode())
                        .errorMessage(message));
    }
}