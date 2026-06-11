package com.gym.crm.platform.mapper;

import com.gym.crm.platform.dto.request.TrainingRequestDTO;
import com.gym.crm.platform.dto.response.TrainingResponseDTO;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public Training dtoToEntity(TrainingRequestDTO trainingRequestDTO, Trainee trainee, Trainer trainer, TrainingType trainingType) {
        return Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingRequestDTO.getTrainingName())
                .trainingType(trainingType)
                .trainingDate(trainingRequestDTO.getTrainingDate())
                .trainingDuration(trainingRequestDTO.getTrainingDuration())
                .build();
    }

    public TrainingResponseDTO entityToDto(Training training) {
        return TrainingResponseDTO.builder()
                .traineeId(training.getTrainee().getId())
                .trainerId(training.getTrainer().getId())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }
}