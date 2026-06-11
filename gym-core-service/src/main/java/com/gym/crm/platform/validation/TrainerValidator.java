package com.gym.crm.platform.validation;

import com.gym.crm.platform.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerValidator {

    public void validateForCreate(Trainer trainer) {
        validateBaseFields(trainer);
        validateSpecialization(trainer);
    }

    public void validateForUpdate(Trainer trainer) {
        validateBaseFields(trainer);
        validateSpecialization(trainer);
    }

    public void validateUsername(String username) {
        Validation.requireNotBlank(username, "Username");
    }

    public void validatePassword(String password) {
        Validation.requireNotBlank(password, "Password");
    }

    public void validateNewPassword(String newPassword) {
        Validation.requireNotBlank(newPassword, "New password");
    }

    private void validateBaseFields(Trainer trainer) {
        Validation.requireNotNull(trainer, "Trainer");
        Validation.requireNotNull(trainer.getUser(), "User");
        Validation.requireNotBlank(trainer.getUser().getFirstName(), "First name");
        Validation.requireNotBlank(trainer.getUser().getLastName(), "Last name");
    }

    private void validateSpecialization(Trainer trainer) {
        Validation.requireNotNull(trainer.getSpecialization(), "Specialization");
        Validation.requireNotBlank(trainer.getSpecialization().getTrainingTypeName(), "Specialization");
    }
}