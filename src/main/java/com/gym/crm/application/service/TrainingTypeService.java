package com.gym.crm.application.service;

import com.gym.crm.application.entity.TrainingType;
import java.util.List;

public interface TrainingTypeService {
    List<TrainingType> getAllTrainingsType();

    TrainingType getByName(String name);
}