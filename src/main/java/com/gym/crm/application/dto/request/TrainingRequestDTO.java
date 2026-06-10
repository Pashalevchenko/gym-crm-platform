package com.gym.crm.application.dto.request;

import com.gym.crm.application.entity.TrainingType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class TrainingRequestDTO {

    @Positive(message = "Trainee ID must be positive")
    private final long traineeId;

    @Positive(message = "Trainer ID must be positive")
    private final long trainerId;

    @NotBlank(message = "Training name is required")
    @Size(max = 100, message = "Training name cannot exceed 100 characters")
    private final String trainingName;

    @NotNull(message = "Training type is required")
    private final TrainingType trainingType;

    @FutureOrPresent(message = "Training date cannot be in the past")
    private final LocalDate trainingDate;

    @Positive(message = "Duration must be greater than zero")
    private final int trainingDuration;
}