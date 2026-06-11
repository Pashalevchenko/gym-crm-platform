package com.gym.crm.platform.service.impl;

import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.repository.TrainingTypeRepository;
import com.gym.crm.platform.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<TrainingType> getAllTrainingsType() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingType getByName(String name) {
        return repository.findByTrainingTypeName(name)
                .orElseThrow(() -> new NoSuchElementException(String.format("Training type %s not found", name)));
    }
}