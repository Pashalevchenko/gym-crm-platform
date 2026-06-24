package gym.crm.platform.workload.messaging;

import gym.crm.platform.workload.model.ActionType;

import java.time.LocalDate;

public record TrainerWorkloadMessage(String trainerUsername,
                                     String trainerFirstName,
                                     String trainerLastName,
                                     Boolean isActive,
                                     LocalDate trainingDate,
                                     Integer trainingDuration,
                                     ActionType actionType) {
}
