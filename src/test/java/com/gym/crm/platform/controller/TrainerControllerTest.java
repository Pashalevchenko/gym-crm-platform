package com.gym.crm.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.platform.facade.GymAppFacade;
import com.gym.crm.platform.openapi.ActivationStatusRequest;
import com.gym.crm.platform.openapi.AssignedTraineeResponse;
import com.gym.crm.platform.openapi.GetTrainerTrainingResponse;
import com.gym.crm.platform.openapi.TrainerCreateRequest;
import com.gym.crm.platform.openapi.TrainerCreateResponse;
import com.gym.crm.platform.openapi.TrainerGetResponse;
import com.gym.crm.platform.openapi.TrainerUpdateRequest;
import com.gym.crm.platform.openapi.TrainerUpdateResponse;
import com.gym.crm.platform.security.GymUserDetailsService;
import com.gym.crm.platform.security.JwtService;
import com.gym.crm.platform.security.TokenBlacklistService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    private static final String BASE_URL = "/api/v1/trainers";
    private static final String USERNAME = "test.trainer";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "Trainer";
    private static final String SPECIALIZATION = "Yoga";

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
        TrainerCreateRequest request = new TrainerCreateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(SPECIALIZATION);
        TrainerCreateResponse response = new TrainerCreateResponse()
                .username(USERNAME)
                .password("password");

        when(facade.createTrainer(any(TrainerCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).createTrainer(any(TrainerCreateRequest.class));
    }

    @Test
    void getTrainerProfile_shouldReturnOk() throws Exception {
        AssignedTraineeResponse trainee = new AssignedTraineeResponse()
                .username("test.trainee")
                .firstName("Test")
                .lastName("Trainee");
        TrainerGetResponse response = new TrainerGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true)
                .trainees(List.of(trainee));

        when(facade.getTrainerByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).getTrainerByUsername(USERNAME);
    }

    @Test
    void updateTrainerProfile_shouldReturnOk() throws Exception {
        TrainerUpdateRequest request = new TrainerUpdateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);
        TrainerUpdateResponse response = new TrainerUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true);

        when(facade.updateTrainer(any(TrainerUpdateRequest.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), any(String.class));
    }

    @Test
    void getTrainerTrainings_shouldReturnOk() throws Exception {
        GetTrainerTrainingResponse training = new GetTrainerTrainingResponse()
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.of(2026, 4, 10))
                .trainingDuration(60)
                .trainingType("Yoga")
                .traineeName("test.trainee");

        when(facade.getTrainerTrainings(any(String.class), any(LocalDate.class), any(LocalDate.class), any(String.class)))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2026-04-01")
                        .param("toDate", "2026-04-30")
                        .param("traineeName", "Test"))
                .andExpect(status().isOk());

        verify(facade).getTrainerTrainings(USERNAME, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), "Test");
    }

    @Test
    void toggleActive_shouldReturnOk() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(true);

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).changeTrainerActiveStatus(any(String.class), any(ActivationStatusRequest.class));
    }
}