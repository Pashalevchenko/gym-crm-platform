package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TraineeRepository;
import com.gym.crm.application.repository.TrainerRepository;
import com.gym.crm.application.repository.TrainingTypeRepository;
import com.gym.crm.application.repository.UserRepository;
import com.gym.crm.application.security.GymUserDetailsService;
import com.gym.crm.application.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "management.endpoints.web.exposure.include=health",
        "management.endpoint.health.show-details=always",
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@DisplayName("Health endpoint tests")
class HealthEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeRepository traineeRepository;

    @MockitoBean
    private TrainerRepository trainerRepository;

    @MockitoBean
    private TrainingTypeRepository trainingTypeRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private GymUserDetailsService gymUserDetailsService;

    @Test
    @DisplayName("Should return trainee health details")
    void health_traineeEndpoint_shouldReturnDetails() throws Exception {
        when(traineeRepository.count()).thenReturn(2L);

        mockMvc.perform(get("/actuator/health/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.repository").value("Available"))
                .andExpect(jsonPath("$.details.traineesCount").value(2));
    }

    @Test
    @DisplayName("Should return trainer health details")
    void health_trainerEndpoint_shouldReturnDetails() throws Exception {
        when(trainerRepository.count()).thenReturn(3L);

        mockMvc.perform(get("/actuator/health/trainer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.repository").value("Available"))
                .andExpect(jsonPath("$.details.trainersCount").value(3));
    }

    @Test
    @DisplayName("Should return training type health details")
    void health_trainingTypeEndpoint_shouldReturnDetails() throws Exception {
        when(trainingTypeRepository.count()).thenReturn(2L);

        mockMvc.perform(get("/actuator/health/trainingType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.trainingTypesCount").value(2));
    }

    @Test
    @DisplayName("Should return user repository health details")
    void health_userRepositoryEndpoint_shouldReturnDetails() throws Exception {
        when(userRepository.count()).thenReturn(4L);

        mockMvc.perform(get("/actuator/health/userRepository"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.usersCount").value(4));
    }
}