package com.gym.crm.platform.messaging.workload;

import java.util.List;

public record WorkloadUpdateEvent(List<TrainerWorkloadMessage> messages) {
}