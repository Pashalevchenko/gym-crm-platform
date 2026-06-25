package com.gym.crm.platform.messaging.workload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Workload message producer tests")
class WorkloadMessageProducerTest {

    private static final String QUEUE_NAME = "trainer.workload.queue";
    private static final String PAYLOAD = "{\"trainerUsername\":\"trainer.user\"}";

    @Mock
    private JmsTemplate template;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private WorkloadMessageProducer producer;

    @Test
    @DisplayName("Should serialize and send trainer workload message")
    void send_shouldSerializeAndSendTrainerWorkloadMessage() throws JsonProcessingException {
        TrainerWorkloadMessage message = createMessage();
        ReflectionTestUtils.setField(producer, "trainerWorkloadQueue", QUEUE_NAME);

        when(mapper.writeValueAsString(message)).thenReturn(PAYLOAD);

        producer.send(message);

        verify(mapper).writeValueAsString(message);
        verify(template).convertAndSend(QUEUE_NAME, PAYLOAD);
    }

    @Test
    @DisplayName("Should throw exception when message serialization fails")
    void send_whenSerializationFails_shouldThrowException() throws JsonProcessingException {
        TrainerWorkloadMessage message = createMessage();

        when(mapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("serialization failed") {
        });

        assertThatThrownBy(() -> producer.send(message))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to serialize trainer workload message");
        verify(mapper).writeValueAsString(message);
    }

    private TrainerWorkloadMessage createMessage() {
        return new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}