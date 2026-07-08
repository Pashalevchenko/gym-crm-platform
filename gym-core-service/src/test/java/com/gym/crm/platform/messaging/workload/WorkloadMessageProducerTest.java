package com.gym.crm.platform.messaging.workload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        when(mapper.writeValueAsString(message)).thenReturn(PAYLOAD);

        producer.send(message);

        verify(template).convertAndSend(eq(QUEUE_NAME), eq(PAYLOAD), postProcessorCaptor.capture());
        assertThat(postProcessorCaptor.getValue()).isNotNull();
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

    @Test
    @DisplayName("Should add transaction id to JMS message")
    void send_whenTransactionIdExists_shouldAddTransactionIdToJmsMessage() throws Exception {
        TrainerWorkloadMessage message = createMessage();
        Message jmsMessage = mock(Message.class);
        ReflectionTestUtils.setField(producer, "trainerWorkloadQueue", QUEUE_NAME);
        MDC.put("transactionId", "transaction-123");

        when(mapper.writeValueAsString(message)).thenReturn(PAYLOAD);

        producer.send(message);

        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(template).convertAndSend(eq(QUEUE_NAME), eq(PAYLOAD), captor.capture());
        captor.getValue().postProcessMessage(jmsMessage);
        verify(jmsMessage).setStringProperty("transactionId", "transaction-123");
    }

    private TrainerWorkloadMessage createMessage() {
        return new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}