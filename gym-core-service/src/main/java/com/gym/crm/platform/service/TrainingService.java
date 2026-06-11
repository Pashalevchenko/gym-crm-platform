package com.gym.crm.platform.service;

import com.gym.crm.platform.entity.Training;
import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);

    Training getTrainingById(Long id);

    List<Training> getAllTrainings();
}