package com.gym.crm.platform.client.workload;

import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import com.gym.crm.platform.exception.WorkloadServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class WorkloadServiceClient {

    private final RestClient client;

    @CircuitBreaker(name = "workloadServiceCircuitBreaker", fallbackMethod = "fallbackUpdateTrainerWorkload")
    public void updateTrainerWorkload(TrainerWorkloadRequest request) {
        client.put()
                .uri("/trainer-workloads")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private void fallbackUpdateTrainerWorkload(TrainerWorkloadRequest request, Exception exception) {
        throw new WorkloadServiceUnavailableException("workload-service is temporarily unavailable", exception);
    }
}