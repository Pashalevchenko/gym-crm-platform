package com.gym.crm.platform.service;

import com.gym.crm.platform.entity.TrainingType;
import java.util.List;

public interface TrainingTypeService {
    List<TrainingType> getAllTrainingsType();

    TrainingType getByName(String name);
}