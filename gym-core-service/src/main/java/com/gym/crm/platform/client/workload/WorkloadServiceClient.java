package com.gym.crm.platform.client.workload;

import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class WorkloadServiceClient {

    private final RestClient client;

    public void updateTrainerWorkload(TrainerWorkloadRequest request) {
        client.put()
                .uri("/trainer-workloads")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}