package gym.crm.platform.workload.messaging;

import gym.crm.platform.workload.openapi.ActionType;
import gym.crm.platform.workload.openapi.TrainerWorkloadRequest;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageMapper {

    public TrainerWorkloadRequest toRequest(TrainerWorkloadMessage message) {
        return new TrainerWorkloadRequest()
                .trainerUsername(message.trainerUsername())
                .trainerFirstName(message.trainerFirstName())
                .trainerLastName(message.trainerLastName())
                .isActive(message.isActive())
                .trainingDate(message.trainingDate())
                .trainingDuration(message.trainingDuration())
                .actionType(ActionType.fromValue(message.actionType().name()));
    }
}