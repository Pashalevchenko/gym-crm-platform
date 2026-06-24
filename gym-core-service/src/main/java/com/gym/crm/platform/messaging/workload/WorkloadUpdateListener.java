package com.gym.crm.platform.messaging.workload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WorkloadUpdateListener {

    private final WorkloadMessageProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WorkloadUpdateEvent event) {
        event.messages().forEach(producer::send);
    }
}