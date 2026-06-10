package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.request.TraineeUpdateDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public Trainee dtoToEntity(TraineeRequestDTO traineeRequestDTO) {
        User user = User.builder()
                .firstName(traineeRequestDTO.getFirstName())
                .lastName(traineeRequestDTO.getLastName())
                .isActive(traineeRequestDTO.isActive())
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(traineeRequestDTO.getDateOfBirth())
                .address(traineeRequestDTO.getAddress())
                .build();
    }

    public Trainee dtoToEntity(TraineeUpdateDTO traineeUpdateDTO) {
        User user = User.builder()
                .firstName(traineeUpdateDTO.getFirstName())
                .lastName(traineeUpdateDTO.getLastName())
                .username(traineeUpdateDTO.getUsername())
                .isActive(traineeUpdateDTO.isActive())
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(traineeUpdateDTO.getDateOfBirth())
                .address(traineeUpdateDTO.getAddress())
                .build();
    }

    public TraineeResponseDTO entityToDto(Trainee trainee) {
        User user = trainee.getUser();

        return TraineeResponseDTO.builder()
                .id(trainee.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isActive(user.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }
}
