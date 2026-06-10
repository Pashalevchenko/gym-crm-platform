package com.gym.crm.platform.mapper.rest;

import com.gym.crm.platform.dto.request.TraineeUpdateDTO;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.openapi.AssignedTrainerResponse;
import com.gym.crm.platform.openapi.GetTraineeTrainingResponse;
import com.gym.crm.platform.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.platform.openapi.TraineeCreateRequest;
import com.gym.crm.platform.openapi.TraineeCreateResponse;
import com.gym.crm.platform.openapi.TraineeGetResponse;
import com.gym.crm.platform.openapi.TraineeUpdateRequest;
import com.gym.crm.platform.openapi.TraineeUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraineeRestMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    Trainee toEntity(TraineeCreateRequest request);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    TraineeCreateResponse toCreateResponse(Trainee trainee);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "dateOfBirth", source = "request.dateOfBirth")
    @Mapping(target = "address", source = "request.address")
    @Mapping(target = "isActive", source = "request.isActive")
    TraineeUpdateDTO toUpdateDto(String username, TraineeUpdateRequest request);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainers", source = "trainers")
    TraineeGetResponse toGetResponse(Trainee trainee);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainers", source = "trainers")
    TraineeUpdateResponse toUpdateResponse(Trainee trainee);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    AssignedTrainerResponse toAssignedTrainerResponse(Trainer trainer);

    List<AssignedTrainerResponse> toAssignedTrainerResponses(List<Trainer> trainers);

    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    @Mapping(target = "trainingType", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainerName", source = "trainer.user.username")
    GetTraineeTrainingResponse toTrainingResponse(Training training);

    List<GetTraineeTrainingResponse> toTrainingResponses(List<Training> trainings);

    default TraineeAssignedTrainersUpdateResponse toAssignedTrainersUpdateResponse(List<AssignedTrainerResponse> trainers) {
        return new TraineeAssignedTrainersUpdateResponse().trainers(trainers);
    }
}