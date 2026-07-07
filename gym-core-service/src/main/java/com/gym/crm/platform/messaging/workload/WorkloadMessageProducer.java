package com.gym.crm.platform.messaging.workload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageProducer {

    private static final String TRANSACTION_ID = "transactionId";

    private final JmsTemplate template;
    private final ObjectMapper mapper;

    @Value("${workload.messaging.queue.trainer-workload}")
    private String trainerWorkloadQueue;

    public void send(TrainerWorkloadMessage message) {
        try {
            String payload = mapper.writeValueAsString(message);
            String transactionId = MDC.get(TRANSACTION_ID);

            template.convertAndSend(trainerWorkloadQueue, payload, transactionIdPostProcessor(transactionId));
            log.info("Trainer workload message sent. queue={}, trainer={}, actionType={}, transactionId={}",
                    trainerWorkloadQueue,
                    message.trainerUsername(),
                    message.actionType(),
                    transactionId);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize trainer workload message", exception);
        }
    }

    private MessagePostProcessor transactionIdPostProcessor(String transactionId) {
        return jmsMessage -> {
            if (transactionId != null && !transactionId.isBlank()) {
                jmsMessage.setStringProperty(TRANSACTION_ID, transactionId);
            }

            return jmsMessage;
        };
    }
}