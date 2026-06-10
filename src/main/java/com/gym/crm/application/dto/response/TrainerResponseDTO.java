package com.gym.crm.application.dto.response;

import com.gym.crm.application.entity.TrainingType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TrainerResponseDTO {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final boolean isActive;
    private final TrainingType specialization;
}
