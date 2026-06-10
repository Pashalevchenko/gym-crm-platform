package com.gym.crm.application.dto.response;

import com.gym.crm.application.entity.TrainingType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Builder
@ToString
public class TrainingResponseDTO {
    private final long traineeId;
    private final long trainerId;
    private final String trainingName;
    private final TrainingType trainingType;
    private final LocalDate trainingDate;
    private final int trainingDuration;
}