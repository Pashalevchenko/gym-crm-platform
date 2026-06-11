package com.gym.crm.platform.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Builder
@ToString
public class TraineeResponseDTO {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final boolean isActive;
    private final LocalDate dateOfBirth;
    private final String address;
}
