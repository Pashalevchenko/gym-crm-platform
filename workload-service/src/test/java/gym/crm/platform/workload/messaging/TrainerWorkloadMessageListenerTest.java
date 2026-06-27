package gym.crm.platform.workload.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gym.crm.platform.workload.exception.InvalidWorkloadMessageException;
import gym.crm.platform.workload.model.ActionType;
import gym.crm.platform.workload.openapi.TrainerWorkloadRequest;
import gym.crm.platform.workload.service.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Trainer workload message listener tests")
class TrainerWorkloadMessageListenerTest {

    private static final String PAYLOAD = "{\"trainerUsername\":\"trainer.user\"}";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TrainerWorkloadMessageMapper messageMapper;

    @Mock
    private TrainerWorkloadMessageValidator validator;

    @Mock
    private TrainerWorkloadServiceImpl service;

    @InjectMocks
    private TrainerWorkloadMessageListener listener;

    @Test
    @DisplayName("Should deserialize message and update trainer workload")
    void handle_shouldDeserializeMessageAndUpdateTrainerWorkload() throws JsonProcessingException {
        TrainerWorkloadMessage message = createMessage();
        TrainerWorkloadRequest request = new TrainerWorkloadRequest()
                .trainerUsername("trainer.user")
                .trainingDuration(60);

        when(objectMapper.readValue(PAYLOAD, TrainerWorkloadMessage.class)).thenReturn(message);
        when(messageMapper.toRequest(message)).thenReturn(request);

        listener.handle(PAYLOAD);

        verify(objectMapper).readValue(PAYLOAD, TrainerWorkloadMessage.class);
        verify(validator).validate(message);
        verify(messageMapper).toRequest(message);
        verify(service).updateTrainerWorkload(request);
    }

    @Test
    @DisplayName("Should not update workload when payload cannot be deserialized")
    void handle_whenPayloadIsInvalid_shouldThrowExceptionAndNotUpdateWorkload() throws JsonProcessingException {
        JsonProcessingException exception = new JsonProcessingException("invalid payload") {
        };

        when(objectMapper.readValue(PAYLOAD, TrainerWorkloadMessage.class)).thenThrow(exception);

        assertThatThrownBy(() -> listener.handle(PAYLOAD))
                .isInstanceOf(InvalidWorkloadMessageException.class)
                .hasMessage("Invalid workload message JSON")
                .hasCause(exception);

        verify(objectMapper).readValue(PAYLOAD, TrainerWorkloadMessage.class);
        verify(messageMapper, never()).toRequest(createMessage());
        verify(service, never()).updateTrainerWorkload(new TrainerWorkloadRequest());
    }

    @Test
    @DisplayName("Should propagate exception when message misses required fields")
    void handle_whenMessageMissesRequiredFields_shouldPropagateException() throws JsonProcessingException {
        TrainerWorkloadMessage message = new TrainerWorkloadMessage(null, null, null, null, null, null, null);
        IllegalArgumentException exception = new IllegalArgumentException("Required workload message field is missing: trainerUsername");

        when(objectMapper.readValue(PAYLOAD, TrainerWorkloadMessage.class)).thenReturn(message);
        org.mockito.Mockito.doThrow(exception).when(validator).validate(message);

        assertThatThrownBy(() -> listener.handle(PAYLOAD)).isSameAs(exception);
        verify(objectMapper).readValue(PAYLOAD, TrainerWorkloadMessage.class);
        verify(validator).validate(message);
        verify(messageMapper, never()).toRequest(any());
        verify(service, never()).updateTrainerWorkload(any());
    }

    private TrainerWorkloadMessage createMessage() {
        return new TrainerWorkloadMessage("trainer.user", "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}
