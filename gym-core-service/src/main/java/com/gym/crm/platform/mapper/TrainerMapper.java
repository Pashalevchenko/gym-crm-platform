package com.gym.crm.platform.mapper;

import com.gym.crm.platform.dto.request.TrainerRequestDTO;
import com.gym.crm.platform.dto.request.TrainerUpdateDTO;
import com.gym.crm.platform.dto.response.TrainerResponseDTO;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer dtoToEntity(TrainerRequestDTO trainerRequestDTO) {
        User user = User.builder()
                .firstName(trainerRequestDTO.getFirstName())
                .lastName(trainerRequestDTO.getLastName())
                .isActive(trainerRequestDTO.isActive())
                .build();
        TrainingType specialization = TrainingType.builder()
                .trainingTypeName(trainerRequestDTO.getSpecialization().getTrainingTypeName())
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    public Trainer dtoToEntity(TrainerUpdateDTO trainerUpdateDTO) {
        User user = User.builder()
                .firstName(trainerUpdateDTO.getFirstName())
                .lastName(trainerUpdateDTO.getLastName())
                .username(trainerUpdateDTO.getUsername())
                .isActive(trainerUpdateDTO.isActive())
                .build();
        TrainingType specialization = TrainingType.builder()
                .trainingTypeName(trainerUpdateDTO.getSpecialization().getTrainingTypeName())
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    public TrainerResponseDTO entityToDto(Trainer trainer) {
        User user = trainer.getUser();

        return TrainerResponseDTO.builder()
                .id(trainer.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isActive(user.isActive())
                .specialization(trainer.getSpecialization())
                .build();
    }
}