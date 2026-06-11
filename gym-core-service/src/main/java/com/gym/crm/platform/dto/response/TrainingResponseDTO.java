package com.gym.crm.platform.dto.response;

import com.gym.crm.platform.entity.TrainingType;
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