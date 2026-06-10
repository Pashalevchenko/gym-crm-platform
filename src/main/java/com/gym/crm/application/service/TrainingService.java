package com.gym.crm.application.service;

import com.gym.crm.application.entity.Training;
import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);

    Training getTrainingById(Long id);

    List<Training> getAllTrainings();
}