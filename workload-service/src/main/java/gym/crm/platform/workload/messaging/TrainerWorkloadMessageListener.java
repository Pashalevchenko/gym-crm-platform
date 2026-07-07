package gym.crm.platform.workload.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gym.crm.platform.workload.exception.InvalidWorkloadMessageException;
import gym.crm.platform.workload.exception.WorkloadMessageProcessingException;
import gym.crm.platform.workload.service.TrainerWorkloadServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageListener {

    private static final int MAX_PAYLOAD_LENGTH = 1000;
    private static final String TRANSACTION_ID = "transactionId";

    private final ObjectMapper objectMapper;
    private final TrainerWorkloadMessageValidator validator;
    private final TrainerWorkloadMessageMapper messageMapper;
    private final TrainerWorkloadServiceImpl service;

    @JmsListener(destination = "${workload.messaging.queue.trainer-workload}")
    public void handle(String payload, @Header(name = TRANSACTION_ID, required = false) String transactionId) {
        String resolvedTransactionId = resolveTransactionId(transactionId);

        try (Closeable ignored = MDC.putCloseable(TRANSACTION_ID, resolvedTransactionId)) {
            TrainerWorkloadMessage message = readMessage(payload);

            validator.validate(message);
            processMessage(message);

            log.info("Trainer workload message consumed. trainer={}, actionType={}, transactionId={}",
                    message.trainerUsername(),
                    message.actionType(),
                    resolvedTransactionId);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to close transaction MDC", exception);
        }
    }

    private TrainerWorkloadMessage readMessage(String payload) {
        try {
            return objectMapper.readValue(payload, TrainerWorkloadMessage.class);
        } catch (JsonProcessingException exception) {
            log.warn("Invalid workload message JSON received. payload={}", shorten(payload), exception);
            throw new InvalidWorkloadMessageException("Invalid workload message JSON", exception);
        }
    }

    private void processMessage(TrainerWorkloadMessage message) {
        try {
            service.updateTrainerWorkload(messageMapper.toRequest(message));
        } catch (RuntimeException exception) {
            log.error("Failed to process workload message. trainer={}, actionType={}, reason={}, transactionId={}",
                    message.trainerUsername(),
                    message.actionType(),
                    exception.getMessage(),
                    MDC.get(TRANSACTION_ID),
                    exception);

            throw new WorkloadMessageProcessingException("Failed to process workload message", exception);
        }
    }

    private String resolveTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return transactionId;
    }

    private String shorten(String payload) {
        if (payload == null || payload.length() <= MAX_PAYLOAD_LENGTH) {
            return payload;
        }

        return payload.substring(0, MAX_PAYLOAD_LENGTH);
    }
}