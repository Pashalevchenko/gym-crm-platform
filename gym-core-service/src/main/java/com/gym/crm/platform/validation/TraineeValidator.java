package com.gym.crm.platform.validation;

import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class TraineeValidator {

    public void validateForCreate(Trainee trainee) {
        validateBaseFields(trainee);
    }

    public void validateForUpdate(Trainee trainee) {
        validateBaseFields(trainee);
    }

    public void validateUsername(String username) {
        Validation.requireNotBlank(username, "Username");
    }

    public void validatePassword(String password) {
        Validation.requireNotBlank(password, "Password");
    }

    public void validateTrainersList(Set<Trainer> trainers) {
        Validation.requireNotNull(trainers, "Trainers list");

        trainers.forEach(trainer -> {
            Validation.requireNotNull(trainer, "Trainer");
            Validation.requireNotNull(trainer.getId(), "Trainer id");
        });
    }

    public void validateNewPassword(String newPassword) {
        Validation.requireNotBlank(newPassword, "New password");
    }

    private void validateBaseFields(Trainee trainee) {
        Validation.requireNotNull(trainee, "Trainee");
        Validation.requireNotNull(trainee.getUser(), "User");
        Validation.requireNotBlank(trainee.getUser().getFirstName(), "First name");
        Validation.requireNotBlank(trainee.getUser().getLastName(), "Last name");
    }
}