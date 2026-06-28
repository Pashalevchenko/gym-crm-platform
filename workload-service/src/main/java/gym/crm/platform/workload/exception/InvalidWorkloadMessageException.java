package gym.crm.platform.workload.exception;

public class InvalidWorkloadMessageException extends RuntimeException {

    public InvalidWorkloadMessageException(String message) {
        super(message);
    }

    public InvalidWorkloadMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}