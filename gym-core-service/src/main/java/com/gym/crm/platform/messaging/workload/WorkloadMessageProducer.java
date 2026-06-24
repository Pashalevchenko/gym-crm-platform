package com.gym.crm.platform.messaging.workload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageProducer {

    private final JmsTemplate template;
    private final ObjectMapper mapper;

    @Value("${workload.messaging.queue.trainer-workload}")
    private String trainerWorkloadQueue;

    public void send(TrainerWorkloadMessage message) {
        try {
            String payload = mapper.writeValueAsString(message);
            template.convertAndSend(trainerWorkloadQueue, payload);

            log.info("Trainer workload message sent. queue={}, trainer={}, actionType={}",
                    trainerWorkloadQueue,
                    message.trainerUsername(),
                    message.actionType());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize trainer workload message", exception);
        }
    }
}