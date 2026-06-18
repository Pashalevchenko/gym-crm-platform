package com.gym.crm.platform.client.workload;

import com.gym.crm.platform.client.workload.model.ActionType;
import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class WorkloadRequestMapper {

    public TrainerWorkloadRequest toRequest(Training training, ActionType actionType) {
        Trainer trainer = training.getTrainer();
        User user = trainer.getUser();

        return new TrainerWorkloadRequest(user.getUsername(),
                                          user.getFirstName(),
                                          user.getLastName(),
                                          user.isActive(),
                                          training.getTrainingDate(),
                                          training.getTrainingDuration(),
                                          actionType);
    }
}