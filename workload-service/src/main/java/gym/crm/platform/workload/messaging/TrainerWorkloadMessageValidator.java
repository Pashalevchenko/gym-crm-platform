package gym.crm.platform.workload.messaging;

import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageValidator {

    public void validate(TrainerWorkloadMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Trainer workload message is missing");
        }

        validateRequiredText(message.trainerUsername(), "trainerUsername");
        validateRequiredText(message.trainerFirstName(), "trainerFirstName");
        validateRequiredText(message.trainerLastName(), "trainerLastName");
        validateRequiredValue(message.isActive(), "isActive");
        validateRequiredValue(message.trainingDate(), "trainingDate");
        validateRequiredValue(message.actionType(), "actionType");
        validateTrainingDuration(message.trainingDuration());
    }

    private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(String.format("Required workload message field is missing: %s", fieldName));
        }
    }

    private void validateRequiredValue(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("Required workload message field is missing: %s", fieldName));
        }
    }

    private void validateTrainingDuration(Integer trainingDuration) {
        validateRequiredValue(trainingDuration, "trainingDuration");

        if (trainingDuration <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }
}