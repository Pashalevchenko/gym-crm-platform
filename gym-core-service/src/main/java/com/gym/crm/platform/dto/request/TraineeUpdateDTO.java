package com.gym.crm.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class TraineeUpdateDTO {

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

    @Past(message = "Date of birth must be in the past")
    private final LocalDate dateOfBirth;

    @Size(max = 150, message = "Address cannot be longer than 150 characters")
    private final String address;

    @Size(max = 50, message = "password cannot exceed characters")
    private final String password;
}