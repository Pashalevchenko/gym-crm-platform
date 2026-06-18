package com.gym.crm.platform.client.workload;

import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import java.util.List;

public record WorkloadUpdateEvent(List<TrainerWorkloadRequest> requests) {
}