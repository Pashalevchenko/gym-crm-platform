package com.gym.crm.platform.messaging.workload;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Workload update listener tests")
class WorkloadUpdateListenerTest {

    @Mock
    private WorkloadMessageProducer producer;

    @InjectMocks
    private WorkloadUpdateListener listener;

    @Test
    @DisplayName("Should send all workload messages")
    void handle_shouldSendAllWorkloadMessages() {
        TrainerWorkloadMessage first = createMessage("trainer.first");
        TrainerWorkloadMessage second = createMessage("trainer.second");

        listener.handle(new WorkloadUpdateEvent(List.of(first, second)));

        verify(producer).send(first);
        verify(producer).send(second);
    }

    private TrainerWorkloadMessage createMessage(String username) {
        return new TrainerWorkloadMessage(username, "Trainer", "User", true, LocalDate.of(2026, Month.JUNE, 10), 60, ActionType.ADD);
    }
}
