package com.gym.crm.application.mapper.rest;

import com.gym.crm.application.dto.request.TrainerUpdateDTO;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.openapi.AssignedTraineeResponse;
import com.gym.crm.application.openapi.GetTrainerTrainingResponse;
import com.gym.crm.application.openapi.TrainerCreateRequest;
import com.gym.crm.application.openapi.TrainerCreateResponse;
import com.gym.crm.application.openapi.TrainerGetResponse;
import com.gym.crm.application.openapi.TrainerUpdateRequest;
import com.gym.crm.application.openapi.TrainerUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "specialization.trainingTypeName", source = "specialization")
    Trainer toEntity(TrainerCreateRequest request);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    TrainerCreateResponse toCreateResponse(Trainer trainer);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "isActive", source = "request.isActive")
    TrainerUpdateDTO toUpdateDto(String username, TrainerUpdateRequest request);

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerGetResponse toGetResponse(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization.trainingTypeName")
    @Mapping(target = "isActive", source = "user.active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerUpdateResponse toUpdateResponse(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    AssignedTraineeResponse toAssignedTraineeResponse(com.gym.crm.application.entity.Trainee trainee);

    List<AssignedTraineeResponse> toAssignedTraineeResponses(List<com.gym.crm.application.entity.Trainee> trainees);

    @Mapping(target = "trainingName", source = "training.trainingName")
    @Mapping(target = "trainingDate", source = "training.trainingDate")
    @Mapping(target = "trainingDuration", source = "training.trainingDuration")
    @Mapping(target = "trainingType", source = "training.trainingType.trainingTypeName")
    @Mapping(target = "traineeName", source = "training.trainee.user.username")
    GetTrainerTrainingResponse toTrainingResponse(Training training);

    List<GetTrainerTrainingResponse> toTrainingResponses(List<Training> trainings);
}