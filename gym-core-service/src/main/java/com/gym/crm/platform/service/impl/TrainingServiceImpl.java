package com.gym.crm.platform.service.impl;

import com.gym.crm.platform.actuator.metrics.MetricsService;
import com.gym.crm.platform.client.workload.WorkloadRequestMapper;
import com.gym.crm.platform.client.workload.WorkloadUpdateEvent;
import com.gym.crm.platform.client.workload.model.ActionType;
import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.repository.TrainingRepository;
import com.gym.crm.platform.service.TrainingService;
import com.gym.crm.platform.validation.TrainingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository repository;
    private final TrainingValidator validator;
    private final MetricsService metrics;
    private final WorkloadRequestMapper requestMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Override
    public Training createTraining(Training training) {
        validator.validateForCreate(training);

        Training created = repository.save(training);

        TrainerWorkloadRequest workloadRequest = requestMapper.toRequest(created, ActionType.ADD);
        publisher.publishEvent(new WorkloadUpdateEvent(List.of(workloadRequest)));

        metrics.incrementTrainingCreated();
        log.info("Training created with id: {}", created.getId());
        return created;
    }

    @Transactional(readOnly = true)
    @Override
    public Training getTrainingById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainer with ID %d not found", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> getAllTrainings() {
        return repository.findAll();
    }
}