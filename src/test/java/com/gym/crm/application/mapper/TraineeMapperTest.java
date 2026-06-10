package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.request.TraineeUpdateDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraineeMapperTest {

    private final Long TRAINEE_ID = 100L;
    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapper();
    }

    @Test
    @DisplayName("Should correctly map all provided fields from TraineeRequestDTO to Trainee entity")
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TraineeRequestDTO expected  = TraineeRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv")
                .build();

        Trainee actual = traineeMapper.dtoToEntity(expected );

        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(expected .getFirstName(), actual.getUser().getFirstName());
        assertEquals(expected .getLastName(), actual.getUser().getLastName());
        assertEquals(expected .isActive(), actual.getUser().isActive());
        assertEquals(expected .getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected .getAddress(), actual.getAddress());

        assertNull(actual.getId());
        assertNull(actual.getUser().getUsername());
        assertNull(actual.getUser().getPassword());
    }

    @Test
    @DisplayName("Should correctly map all provided fields from TraineeUpdateDTO to Trainee entity")
    void dtoToEntity_ShouldMapAllFieldsFromUpdateDtoCorrectly() {
        TraineeUpdateDTO expected = TraineeUpdateDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv")
                .build();

        Trainee actual = traineeMapper.dtoToEntity(expected);

        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(expected.getFirstName(), actual.getUser().getFirstName());
        assertEquals(expected.getLastName(), actual.getUser().getLastName());
        assertEquals(expected.getUsername(), actual.getUser().getUsername());
        assertEquals(expected.isActive(), actual.getUser().isActive());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertNull(actual.getId());
        assertNull(actual.getUser().getPassword());
    }

    @Test
    @DisplayName("Should correctly map all fields from Trainee entity to Response DTO including generated values")
    void entityToDto_shouldMapAllFieldsCorrectly() {
        User user = User.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .build();
        Trainee expected = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1960, 4, 15))
                .address("Ivano-Frankivsk")
                .build();

        TraineeResponseDTO actual = traineeMapper.entityToDto(expected);

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getFirstName(), actual.getFirstName());
        assertEquals(expected.getUser().getLastName(), actual.getLastName());
        assertEquals(expected.getUser().getUsername(), actual.getUsername());
        assertEquals(expected.getUser().isActive(), actual.isActive());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getAddress(), actual.getAddress());
    }
}