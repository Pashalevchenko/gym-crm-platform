package com.gym.crm.platform.mapper.rest;

import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.openapi.TrainingCreateRequest;
import com.gym.crm.platform.openapi.TrainingTypeResponse;
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