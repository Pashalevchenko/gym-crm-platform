package com.gym.crm.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.platform.security.GymUserDetailsService;
import com.gym.crm.platform.security.JwtService;
import com.gym.crm.platform.security.TokenBlacklistService;
import com.gym.crm.platform.utils.JsonResourceReader;
import com.gym.crm.platform.facade.GymAppFacade;
import com.gym.crm.platform.openapi.ActivationStatusRequest;
import com.gym.crm.platform.openapi.AssignedTrainerResponse;
import com.gym.crm.platform.openapi.GetTraineeTrainingResponse;
import com.gym.crm.platform.openapi.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.platform.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.platform.openapi.TraineeCreateRequest;
import com.gym.crm.platform.openapi.TraineeCreateResponse;
import com.gym.crm.platform.openapi.TraineeGetResponse;
import com.gym.crm.platform.openapi.TraineeUpdateRequest;
import com.gym.crm.platform.openapi.TraineeUpdateResponse;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static final String USERNAME = "test.user";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";

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
        String request = JsonResourceReader.readResource("/json/trainee-create-request.json");
        String expectedResponse = JsonResourceReader.readResource("/json/trainee-create-response.json");
        TraineeCreateResponse response = new TraineeCreateResponse()
                .username(USERNAME)
                .password("password");

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        String actualResponse = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponse,
                actualResponse,
                JSONCompareMode.STRICT);
        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void getTraineeProfile_shouldReturnOk() throws Exception {
        TraineeGetResponse response = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .isActive(true);

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void updateTraineeProfile_shouldReturnOk() throws Exception {
        String request = JsonResourceReader.readResource("/json/trainee-update-request.json");
        String expectedResponse = JsonResourceReader.readResource("/json/trainee-update-response.json");
        TraineeUpdateResponse response = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Lviv")
                .isActive(false);

        when(facade.updateTrainee(any(TraineeUpdateRequest.class), any(String.class))).thenReturn(response);

        String actualResponse = mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(mapper.readTree(actualResponse)).isEqualTo(mapper.readTree(expectedResponse));
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), any(String.class));
    }

    @Test
    void deleteTrainee_shouldReturnOk() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void toggleActive_shouldReturnOk() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(true);

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).changeActiveStatus(any(String.class), any(ActivationStatusRequest.class));
    }

    @Test
    void getTraineeTrainings_shouldReturnOk() throws Exception {
        GetTraineeTrainingResponse training = new GetTraineeTrainingResponse()
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.of(2026, 4, 10))
                .trainingDuration(60)
                .trainingType("Yoga")
                .trainerName("trainer.user");

        when(facade.getTraineeTrainings(any(String.class),
                any(LocalDate.class),
                any(LocalDate.class),
                any(String.class),
                any(String.class)))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2026-04-01")
                        .param("toDate", "2026-04-30")
                        .param("trainerName", "Trainer")
                        .param("trainingType", "Yoga"))
                .andExpect(status().isOk());

        verify(facade).getTraineeTrainings(USERNAME, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), "Trainer", "Yoga");
    }

    @Test
    void updateTraineeTrainers_shouldReturnOk() throws Exception {
        TraineeAssignedTrainersUpdateRequest request =
                new TraineeAssignedTrainersUpdateRequest()
                        .trainerUsernames(List.of("trainer.user"));
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username("trainer.user")
                .firstName("Trainer")
                .lastName("User")
                .specialization("Yoga");
        TraineeAssignedTrainersUpdateResponse response =
                new TraineeAssignedTrainersUpdateResponse()
                        .trainers(List.of(trainerResponse));

        when(facade.updateTraineeTrainersList(any(String.class), any(TraineeAssignedTrainersUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).updateTraineeTrainersList(any(String.class), any(TraineeAssignedTrainersUpdateRequest.class));
    }

    @Test
    void getAvailableTrainers_shouldReturnOk() throws Exception {
        AssignedTrainerResponse trainer = new AssignedTrainerResponse()
                .username("trainer.user")
                .firstName("Trainer")
                .lastName("User")
                .specialization("Yoga");

        when(facade.getNotAssignedTrainers(USERNAME)).thenReturn(List.of(trainer));

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk());
        verify(facade).getNotAssignedTrainers(USERNAME);
    }
}