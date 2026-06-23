package gym.crm.platform.workload.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {

    VALIDATION_ERROR(2760, HttpStatus.BAD_REQUEST, "Validation error"),
    NOT_FOUND_ERROR(2835, HttpStatus.NOT_FOUND, "Requested data was not found"),
    SERVICE_ERROR(3200, HttpStatus.INTERNAL_SERVER_ERROR, "Internal processing error"),
    DATABASE_ERROR(3358, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected database access failure");

    private final int code;
    private final HttpStatus status;
    private final String message;

    ApiErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}