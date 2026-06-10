package com.gym.crm.platform.mapper;

import com.gym.crm.platform.dto.request.TrainingRequestDTO;
import com.gym.crm.platform.dto.response.TrainingResponseDTO;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingMapperTest {

    private final Long TRAINEE_ID = 1L;
    private final Long TRAINER_ID = 2L;
    private final Long TRAINING_TYPE_ID = 3L;

    private TrainingMapper trainingMapper;

    @BeforeEach
    void setUp() {
        trainingMapper = new TrainingMapper();
    }

    @Test
    @DisplayName("Should correctly map TrainingRequestDTO to Training entity with relations and session details")
    void dtoToEntity_shouldMapAllFieldsCorrectly() {
        TrainingType trainingType =TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .trainingTypeName("Strength")
                .build();
        TrainingRequestDTO request = TrainingRequestDTO.builder()
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .trainingName("Deadlift Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2026, 5, 20))
                .trainingDuration(90)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();

        Training actual = trainingMapper.dtoToEntity(request, trainee, trainer, trainingType);

        assertNotNull(actual);
        assertEquals(trainee, actual.getTrainee());
        assertEquals(trainer, actual.getTrainer());
        assertEquals(trainingType, actual.getTrainingType());
        assertEquals(request.getTrainingName(), actual.getTrainingName());
        assertEquals(request.getTrainingDate(), actual.getTrainingDate());
        assertEquals(request.getTrainingDuration(), actual.getTrainingDuration());
    }

    @Test
    @DisplayName("Should correctly map Training entity to Response DTO including participant IDs and session details")
    void entityToDto_shouldMapAllFieldsCorrectly() {
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        TrainingType trainingType = TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .trainingTypeName("Cardio")
                .build();
        Training expected = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Run")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2026, 5, 20))
                .trainingDuration(45)
                .build();

        TrainingResponseDTO actual = trainingMapper.entityToDto(expected);

        assertNotNull(actual);
        assertEquals(TRAINEE_ID, actual.getTraineeId());
        assertEquals(TRAINER_ID, actual.getTrainerId());
        assertEquals(expected.getTrainingName(), actual.getTrainingName());
        assertEquals(expected.getTrainingType(), actual.getTrainingType());
        assertEquals(expected.getTrainingDate(), actual.getTrainingDate());
        assertEquals(expected.getTrainingDuration(), actual.getTrainingDuration());
    }
}