package com.gym.crm.application.mapper.rest;

import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.openapi.TrainingCreateRequest;
import com.gym.crm.application.openapi.TrainingTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    @Mapping(target = "trainingName", source = "trainingName")
    @Mapping(target = "trainingDate", source = "trainingDate")
    @Mapping(target = "trainingDuration", source = "trainingDuration")
    Training toEntity(TrainingCreateRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "trainingTypeName")
    TrainingTypeResponse toResponse(TrainingType trainingType);

    List<TrainingTypeResponse> toTrainingTypeResponses(List<TrainingType> trainingTypes);
}