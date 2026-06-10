package com.gym.crm.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.TrainingCreateRequest;
import com.gym.crm.application.openapi.TrainingTypeResponse;
import com.gym.crm.application.security.GymUserDetailsService;
import com.gym.crm.application.security.JwtService;
import com.gym.crm.application.security.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    private static final String BASE_URL = "/api/v1/trainings";
    private static final String TRAINEE_USERNAME = "test.trainee";
    private static final String TRAINER_USERNAME = "test.trainer";

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymAppFacade facade;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private GymUserDetailsService gymUserDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void register_shouldReturnOk() throws Exception {
        TrainingCreateRequest request = new TrainingCreateRequest()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.of(2026, 4, 10))
                .trainingDuration(60);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).createTraining(any(TrainingCreateRequest.class));
    }

    @Test
    void getTrainingTypes_shouldReturnOk() throws Exception {
        TrainingTypeResponse response = new TrainingTypeResponse()
                .id(1)
                .name("Yoga");

        when(facade.getAllTrainingsType()).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL + "/types"))
                .andExpect(status().isOk());
        verify(facade).getAllTrainingsType();
    }
}