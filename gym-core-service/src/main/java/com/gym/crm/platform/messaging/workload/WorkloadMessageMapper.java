package com.gym.crm.platform.messaging.workload;

import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageMapper {

    public TrainerWorkloadMessage toMessage(Training training, ActionType actionType) {
        Trainer trainer = training.getTrainer();
        User user = trainer.getUser();

        return new TrainerWorkloadMessage(user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                actionType);
    }
}