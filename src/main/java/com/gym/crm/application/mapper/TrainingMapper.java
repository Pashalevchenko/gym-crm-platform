package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
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