package com.gym.crm.application.service;

import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.repository.TrainingTypeRepository;
import com.gym.crm.application.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    private static final String TRAINING_TYPE_NAME = "Yoga";

    @Mock
    private TrainingTypeRepository repository;

    @InjectMocks
    private TrainingTypeServiceImpl service;

    @Test
    @DisplayName("Should return all training types")
    void getAllTrainingType_shouldReturnAllTrainingTypes() {
        TrainingType yoga = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();
        TrainingType cardio = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Cardio")
                .build();
        List<TrainingType> expected = List.of(yoga, cardio);

        when(repository.findAll()).thenReturn(expected);

        List<TrainingType> actual = service.getAllTrainingsType();

        assertEquals(expected, actual);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return training type by name")
    void getByName_whenTrainingTypeExists_shouldReturnTrainingType() {
        TrainingType expected = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .build();

        when(repository.findByTrainingTypeName(TRAINING_TYPE_NAME)).thenReturn(Optional.of(expected));

        TrainingType actual = service.getByName(TRAINING_TYPE_NAME);

        assertSame(expected, actual);
        verify(repository).findByTrainingTypeName(TRAINING_TYPE_NAME);
    }

    @Test
    @DisplayName("Should throw exception when training type is not found")
    void getByName_whenTrainingTypeDoesNotExist_shouldThrowException() {
        when(repository.findByTrainingTypeName(TRAINING_TYPE_NAME)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.getByName(TRAINING_TYPE_NAME));

        assertEquals("Training type Yoga not found", exception.getMessage());
        verify(repository).findByTrainingTypeName(TRAINING_TYPE_NAME);
    }
}