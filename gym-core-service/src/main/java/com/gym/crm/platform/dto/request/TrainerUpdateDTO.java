package com.gym.crm.platform.dto.request;

import com.gym.crm.platform.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TrainerUpdateDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 110, message = "Username cannot exceed characters")
    private final String username;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed characters")
    private final String lastName;

    private final boolean isActive;

    @NotNull(message = "Specialization is required")
    private final TrainingType specialization;

    @Size(max = 50, message = "password cannot exceed characters")
    private final String password;
}
