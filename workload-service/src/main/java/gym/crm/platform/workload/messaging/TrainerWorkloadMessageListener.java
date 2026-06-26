package gym.crm.platform.workload.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gym.crm.platform.workload.service.TrainerWorkloadServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadMessageListener {

    private final ObjectMapper objectMapper;
    private final TrainerWorkloadMessageValidator validator;
    private final TrainerWorkloadMessageMapper messageMapper;
    private final TrainerWorkloadServiceImpl service;

    @JmsListener(destination = "${workload.messaging.queue.trainer-workload}")
    public void handle(String payload) throws JsonProcessingException {
        TrainerWorkloadMessage message = objectMapper.readValue(payload, TrainerWorkloadMessage.class);

        validator.validate(message);

        service.updateTrainerWorkload(messageMapper.toRequest(message));
        log.info("Trainer workload message consumed. trainer={}, actionType={}",
                message.trainerUsername(),
                message.actionType());
    }
}