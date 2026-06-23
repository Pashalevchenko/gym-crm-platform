package gym.crm.platform.workload.exception;

import gym.crm.platform.workload.openapi.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Global exception handler tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle method argument not valid exception as validation error")
    void handleMethodArgumentNotValid_shouldReturnValidationError() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError usernameError = new FieldError("trainerWorkloadRequest", "trainerUsername", "must match pattern");
        FieldError durationError = new FieldError("trainerWorkloadRequest", "trainingDuration", "must be greater than or equal to 1");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(usernameError, durationError));

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Validation error: trainerUsername must match pattern, trainingDuration must be greater than or equal to 1",
                response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle handler method validation exception as validation error")
    void handleHandlerMethodValidation_shouldReturnValidationError() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);

        when(exception.getMessage()).thenReturn("Validation failed for argument");

        ResponseEntity<ErrorResponse> response = handler.handleHandlerMethodValidation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Validation error: Validation failed for argument", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle illegal argument as validation error")
    void handleIllegalArgument_shouldReturnValidationError() {
        IllegalArgumentException exception = new IllegalArgumentException("month must be between 1 and 12");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Validation error: month must be between 1 and 12", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle not found exception")
    void handleNotFound_shouldReturnNotFoundError() {
        NoSuchElementException exception = new NoSuchElementException("Trainer workload not found: test.user");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(exception);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2835, response.getBody().getErrorCode());
        assertEquals("Requested data was not found: Trainer workload not found: test.user", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle null pointer exception as validation error")
    void handleNullPointerException_shouldReturnValidationError() {
        NullPointerException exception = new NullPointerException("Cannot invoke field");

        ResponseEntity<ErrorResponse> response = handler.handleNullPointerException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2760, response.getBody().getErrorCode());
        assertEquals("Required request data is missing", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle data access exception as database error")
    void handleDataAccessException_shouldReturnDatabaseError() {
        DataAccessException exception = new DataAccessException("Redis unavailable") {
        };

        ResponseEntity<ErrorResponse> response = handler.handleDataAccessException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3358, response.getBody().getErrorCode());
        assertEquals("Unexpected database access failure: Redis unavailable", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Should handle generic exception as service error")
    void handleGenericException_shouldReturnServiceError() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3200, response.getBody().getErrorCode());
        assertEquals("Internal processing error", response.getBody().getErrorMessage());
    }
}
