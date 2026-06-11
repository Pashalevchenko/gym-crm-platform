package com.gym.crm.platform.facade;

import com.gym.crm.platform.openapi.LoginResponse;
import com.gym.crm.platform.mapper.TraineeMapper;
import com.gym.crm.platform.mapper.TrainerMapper;
import com.gym.crm.platform.mapper.TrainingMapper;
import com.gym.crm.platform.mapper.rest.TraineeRestMapper;
import com.gym.crm.platform.mapper.rest.TrainerRestMapper;
import com.gym.crm.platform.mapper.rest.TrainingRestMapper;
import com.gym.crm.platform.dto.request.TraineeUpdateDTO;
import com.gym.crm.platform.dto.request.TrainerUpdateDTO;
import com.gym.crm.platform.dto.response.TraineeResponseDTO;
import com.gym.crm.platform.dto.response.TrainingResponseDTO;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.entity.User;
import com.gym.crm.platform.openapi.ActivationStatusRequest;
import com.gym.crm.platform.openapi.AssignedTrainerResponse;
import com.gym.crm.platform.openapi.GetTraineeTrainingResponse;
import com.gym.crm.platform.openapi.GetTrainerTrainingResponse;
import com.gym.crm.platform.openapi.LoginChangeRequest;
import com.gym.crm.platform.openapi.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.platform.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.platform.openapi.TraineeCreateRequest;
import com.gym.crm.platform.openapi.TraineeCreateResponse;
import com.gym.crm.platform.openapi.TraineeGetResponse;
import com.gym.crm.platform.openapi.TraineeUpdateRequest;
import com.gym.crm.platform.openapi.TraineeUpdateResponse;
import com.gym.crm.platform.openapi.TrainerCreateRequest;
import com.gym.crm.platform.openapi.TrainerCreateResponse;
import com.gym.crm.platform.openapi.TrainerGetResponse;
import com.gym.crm.platform.openapi.TrainerUpdateRequest;
import com.gym.crm.platform.openapi.TrainerUpdateResponse;
import com.gym.crm.platform.openapi.TrainingCreateRequest;
import com.gym.crm.platform.openapi.TrainingTypeResponse;
import com.gym.crm.platform.service.TraineeService;
import com.gym.crm.platform.service.TrainerService;
import com.gym.crm.platform.service.TrainingService;
import com.gym.crm.platform.service.TrainingTypeService;
import com.gym.crm.platform.service.UserService;
import com.gym.crm.platform.service.common.AuthenticationService;
import com.gym.crm.platform.service.common.LogoutService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.gym.crm.platform.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class GymAppFacadeTest {

    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanov";
    private static final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private static final Long TRAINEE_ID = 1L;
    private static final Long TRAINER_ID = 2L;
    private static final Long TRAINING_ID = 3L;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TraineeRestMapper traineeRestMapper;

    @Mock
    private TrainerRestMapper trainerRestMapper;

    @Mock
    private TrainingRestMapper trainingRestMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private AuthenticationService authService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GymAppFacade facade;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clear();
    }

    @Test
    void loginShouldAuthenticateUserAndSetSecurityContext() {
        String username = "test.user";
        String password = "12345";
        String token = "jwt-token";

        when(authService.authenticate(username, password)).thenReturn(token);

        LoginResponse actual = facade.login(username, password);

        assertThat(actual.getToken()).isEqualTo(token);
        verify(authService).authenticate(username, password);
    }

    @Test
    void loginShouldNotSetSecurityContextWhenAuthenticationFails() {
        String username = "test.user";
        String password = "wrong-password";

        doThrow(new IllegalArgumentException("Invalid username or password"))
                .when(authService)
                .authenticate(username, password);

        assertThatThrownBy(() -> facade.login(username, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        assertThat(SecurityContextHolder.getContext()).isNull();
        verify(authService).authenticate(username, password);
    }

    @Test
    void logoutShouldClearSecurityContext() {
        String token = "jwt-token";
        String authorizationHeader = "Bearer " + token;

        facade.logout(authorizationHeader);

        verify(logoutService).logout(authorizationHeader);
    }

    @Test
    @DisplayName("Verify that facade calls trainee service and uses rest mapper for create operation")
    void createTrainee_Test() {
        TraineeCreateRequest request = new TraineeCreateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME);
        Trainee trainee = Trainee.builder().build();
        User user = User.builder()
                .username(USERNAME)
                .password("generated-password")
                .build();
        Trainee createdTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .build();
        TraineeCreateResponse expected = new TraineeCreateResponse()
                .username(USERNAME)
                .password("generated-password");

        when(traineeRestMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(createdTrainee);
        when(traineeRestMapper.toCreateResponse(createdTrainee)).thenReturn(expected);

        TraineeCreateResponse actual = facade.createTrainee(request);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toEntity(request);
        verify(traineeService).createTrainee(trainee);
        verify(traineeRestMapper).toCreateResponse(createdTrainee);
    }

    @Test
    @DisplayName("Should return trainee DTO when a valid ID is provided to the facade")
    void getTraineeById_Test() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .firstName(FIRST_NAME)
                .build();

        when(traineeService.getTraineeById(TRAINEE_ID)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.getTraineeById(TRAINEE_ID);

        assertEquals(expected, actual);
        verify(traineeService).getTraineeById(TRAINEE_ID);
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should get trainee by username")
    void getTraineeByUsername_shouldReturnMappedDto() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeGetResponse expected = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(trainee);
        when(traineeRestMapper.toGetResponse(trainee)).thenReturn(expected);

        TraineeGetResponse actual = facade.getTraineeByUsername(USERNAME);

        assertEquals(expected, actual);
        verify(traineeService).getTraineeByUsername(USERNAME);
        verify(traineeRestMapper).toGetResponse(trainee);
    }

    @Test
    @DisplayName("Should get all trainees")
    void getAllTrainees_shouldReturnMappedDtoList() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO response = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .username(USERNAME)
                .build();

        when(traineeService.getAllTrainees()).thenReturn(List.of(trainee));
        when(traineeMapper.entityToDto(trainee)).thenReturn(response);

        List<TraineeResponseDTO> actual = facade.getAllTrainees();

        assertEquals(1, actual.size());
        assertEquals(USERNAME, actual.get(0).getUsername());
        verify(traineeService).getAllTrainees();
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should successfully update trainee by mapping request DTO to entity and returning response DTO")
    void updateTrainee_Test() {
        TraineeUpdateRequest request = new TraineeUpdateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false);
        TraineeUpdateDTO dto = TraineeUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        Trainee updated = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        TraineeUpdateResponse expected = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false);

        when(traineeRestMapper.toUpdateDto(USERNAME, request)).thenReturn(dto);
        when(traineeMapper.dtoToEntity(dto)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(updated);
        when(traineeRestMapper.toUpdateResponse(updated)).thenReturn(expected);

        TraineeUpdateResponse actual = facade.updateTrainee(request, USERNAME);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toUpdateDto(USERNAME, request);
        verify(traineeMapper).dtoToEntity(dto);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeRestMapper).toUpdateResponse(updated);
    }

    @Test
    @DisplayName("Should change trainee active status to active")
    void changeActiveStatus_shouldActivateTrainee() {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(true);

        facade.changeActiveStatus(USERNAME, request);

        verify(traineeService).changeActiveStatus(USERNAME, true);
    }

    @Test
    @DisplayName("Should change trainee active status to inactive")
    void changeActiveStatus_shouldDeactivateTrainee() {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(false);

        facade.changeActiveStatus(USERNAME, request);

        verify(traineeService).changeActiveStatus(USERNAME, false);
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_Test() {
        facade.deleteTraineeByUsername(USERNAME);

        verify(traineeService).deleteTraineeByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should get trainee trainings")
    void getTraineeTrainings_shouldReturnMappedTrainingList() {
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 1, 31);
        String trainerName = "Ivan Trainer";
        String trainingTypeName = "Yoga";

        Training training = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Morning Yoga")
                .build();

        GetTraineeTrainingResponse response = new GetTraineeTrainingResponse()
                .trainingName("Morning Yoga");

        when(traineeService.getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName))
                .thenReturn(List.of(training));
        when(traineeRestMapper.toTrainingResponses(List.of(training)))
                .thenReturn(List.of(response));

        List<GetTraineeTrainingResponse> actual =
                facade.getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName);

        assertEquals(1, actual.size());
        assertEquals("Morning Yoga", actual.get(0).getTrainingName());
        verify(traineeService).getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName);
        verify(traineeRestMapper).toTrainingResponses(List.of(training));
    }

    @Test
    @DisplayName("Should get not assigned trainers")
    void getNotAssignedTrainers_shouldReturnMappedTrainerList() {
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        AssignedTrainerResponse response = new AssignedTrainerResponse()
                .username("trainer.username")
                .firstName("Trainer")
                .lastName("Test");

        when(traineeService.getNotAssignedTrainers(USERNAME)).thenReturn(List.of(trainer));
        when(traineeRestMapper.toAssignedTrainerResponses(List.of(trainer))).thenReturn(List.of(response));

        List<AssignedTrainerResponse> actual = facade.getNotAssignedTrainers(USERNAME);

        assertEquals(1, actual.size());
        assertEquals("trainer.username", actual.get(0).getUsername());
        verify(traineeService).getNotAssignedTrainers(USERNAME);
        verify(traineeRestMapper).toAssignedTrainerResponses(List.of(trainer));
    }

    @Test
    @DisplayName("Should update trainee trainers list")
    void updateTraineeTrainersList_shouldDelegateToServiceAndMapResponse() {
        String trainerUsername = "trainer.test";
        User user = User.builder()
                .username(trainerUsername)
                .firstName("Trainer")
                .lastName("Test")
                .build();
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest()
                        .trainerUsernames(List.of(trainerUsername));
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .build();
        Set<Trainer> trainers = Set.of(trainer);
        Trainee updatedTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .trainers(trainers)
                .build();
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username(trainerUsername)
                .firstName("Trainer")
                .lastName("Test");
        TraineeAssignedTrainersUpdateResponse expected =
                new TraineeAssignedTrainersUpdateResponse()
                        .trainers(List.of(trainerResponse));

        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(traineeService.updateTrainersList(USERNAME, trainers)).thenReturn(updatedTrainee);
        when(traineeRestMapper.toAssignedTrainerResponse(trainer)).thenReturn(trainerResponse);
        when(traineeRestMapper.toAssignedTrainersUpdateResponse(List.of(trainerResponse))).thenReturn(expected);

        TraineeAssignedTrainersUpdateResponse actual = facade.updateTraineeTrainersList(USERNAME, request);

        assertEquals(expected, actual);
        verify(trainerService).getTrainerByUsername(trainerUsername);
        verify(traineeService).updateTrainersList(USERNAME, trainers);
        verify(traineeRestMapper).toAssignedTrainerResponse(trainer);
        verify(traineeRestMapper).toAssignedTrainersUpdateResponse(List.of(trainerResponse));
    }

    @Test
    @DisplayName("Should verify the complete flow of trainer creation from REST request to entity and response")
    void createTrainer_Test() {
        TrainerCreateRequest request = new TrainerCreateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization("Yoga");
        Trainer trainer = Trainer.builder().build();
        User user = User.builder()
                .username(USERNAME)
                .password("generated-password")
                .build();
        Trainer createdTrainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .build();
        TrainerCreateResponse expected = new TrainerCreateResponse()
                .username(USERNAME)
                .password("generated-password");

        when(trainerRestMapper.toEntity(request)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(createdTrainer);
        when(trainerRestMapper.toCreateResponse(createdTrainer)).thenReturn(expected);

        TrainerCreateResponse actual = facade.createTrainer(request);

        assertEquals(expected, actual);
        assertEquals(USERNAME, actual.getUsername());
        verify(trainerRestMapper).toEntity(request);
        verify(trainerService).createTrainer(trainer);
        verify(trainerRestMapper).toCreateResponse(createdTrainer);
    }

    @Test
    @DisplayName("Should get trainer profile by username")
    void getTrainerProfile_shouldReturnMappedResponse() {
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        TrainerGetResponse expected = new TrainerGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization("Yoga")
                .isActive(true);

        when(trainerService.getTrainerByUsername(USERNAME)).thenReturn(trainer);
        when(trainerRestMapper.toGetResponse(trainer)).thenReturn(expected);

        TrainerGetResponse actual = facade.getTrainerByUsername(USERNAME);

        assertEquals(expected, actual);
        verify(trainerService).getTrainerByUsername(USERNAME);
        verify(trainerRestMapper).toGetResponse(trainer);
    }

    @Test
    @DisplayName("Should update trainer")
    void updateTrainer_shouldMapRequestCallServiceAndMapResponse() {
        TrainerUpdateRequest request = new TrainerUpdateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);
        TrainerUpdateDTO dto = TrainerUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        Trainer updated = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        TrainerUpdateResponse expected = new TrainerUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization("Yoga")
                .isActive(true);

        when(trainerRestMapper.toUpdateDto(USERNAME, request)).thenReturn(dto);
        when(trainerMapper.dtoToEntity(dto)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(updated);
        when(trainerRestMapper.toUpdateResponse(updated)).thenReturn(expected);

        TrainerUpdateResponse actual = facade.updateTrainer(request, USERNAME);

        assertEquals(expected, actual);
        verify(trainerRestMapper).toUpdateDto(USERNAME, request);
        verify(trainerMapper).dtoToEntity(dto);
        verify(trainerService).updateTrainer(trainer);
        verify(trainerRestMapper).toUpdateResponse(updated);
    }

    @Test
    @DisplayName("Should change trainer active status to active")
    void changeTrainerActiveStatus_shouldActivateTrainer() {
        ActivationStatusRequest request = new ActivationStatusRequest().isActive(true);

        facade.changeTrainerActiveStatus(USERNAME, request);

        verify(trainerService).changeActiveStatus(USERNAME, true);
    }

    @Test
    @DisplayName("Should change trainer active status to inactive")
    void changeTrainerActiveStatus_shouldDeactivateTrainer() {
        ActivationStatusRequest request = new ActivationStatusRequest().isActive(false);

        facade.changeTrainerActiveStatus(USERNAME, request);

        verify(trainerService).changeActiveStatus(USERNAME, false);
    }

    @Test
    @DisplayName("Should get trainer trainings")
    void getTrainerTrainings_shouldReturnMappedTrainingList() {
        LocalDate fromDate = LocalDate.of(2026, 2, 1);
        LocalDate toDate = LocalDate.of(2026, 2, 28);
        String traineeName = "Ivan Trainee";
        Training training = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Boxing")
                .build();
        GetTrainerTrainingResponse response = new GetTrainerTrainingResponse()
                .trainingName("Boxing")
                .trainingDate(LocalDate.of(2026, 2, 10))
                .trainingDuration(60)
                .trainingType("Boxing")
                .traineeName("ivan.trainee");

        when(trainerService.getTrainerTrainings(USERNAME, fromDate, toDate, traineeName)).thenReturn(List.of(training));
        when(trainerRestMapper.toTrainingResponses(List.of(training))).thenReturn(List.of(response));

        List<GetTrainerTrainingResponse> actual = facade.getTrainerTrainings(USERNAME, fromDate, toDate, traineeName);

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getTrainingName());
        verify(trainerService).getTrainerTrainings(USERNAME, fromDate, toDate, traineeName);
        verify(trainerRestMapper).toTrainingResponses(List.of(training));
    }

    @Test
    @DisplayName("Should create training")
    void createTraining_shouldResolveTraineeAndTrainerThenMapAndCreateTraining() {
        TrainingCreateRequest request = new TrainingCreateRequest()
                .traineeUsername("test.trainee")
                .trainerUsername("test.trainer")
                .trainingName("Morning Yoga")
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2026, 4, 10));
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        TrainingType specialization = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .specialization(specialization)
                .build();
        Training mappedTraining = Training.builder()
                .trainingName("Morning Yoga")
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2026, 4, 10))
                .build();

        when(traineeService.getTraineeByUsername("test.trainee")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("test.trainer")).thenReturn(trainer);
        when(trainingRestMapper.toEntity(request)).thenReturn(mappedTraining);

        facade.createTraining(request);

        verify(traineeService).getTraineeByUsername("test.trainee");
        verify(trainerService).getTrainerByUsername("test.trainer");
        verify(trainingRestMapper).toEntity(request);
        verify(trainingService).createTraining(argThat(training ->
                        training.getTrainingName().equals("Morning Yoga") &&
                        training.getTrainingDuration().equals(60) &&
                        training.getTrainingDate().equals(LocalDate.of(2026, 4, 10)) &&
                        training.getTrainee().equals(trainee) &&
                        training.getTrainer().equals(trainer) &&
                        training.getTrainingType().equals(specialization)));
    }

    @Test
    @DisplayName("Should get training by ID")
    void getTrainingById_shouldReturnMappedDto() {
        Training training = Training.builder()
                .id(TRAINING_ID)
                .build();
        TrainingResponseDTO expected = TrainingResponseDTO.builder()
                .trainingName("Morning Yoga")
                .build();

        when(trainingService.getTrainingById(TRAINING_ID)).thenReturn(training);
        when(trainingMapper.entityToDto(training)).thenReturn(expected);

        TrainingResponseDTO actual = facade.getTrainingById(TRAINING_ID);

        assertEquals(expected, actual);
        verify(trainingService).getTrainingById(TRAINING_ID);
        verify(trainingMapper).entityToDto(training);
    }

    @Test
    @DisplayName("Should get all training types")
    void getAllTrainings_shouldReturnMappedTrainingTypeList() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Boxing")
                .build();
        TrainingTypeResponse response = new TrainingTypeResponse()
                .id(1)
                .name("Boxing");

        when(trainingTypeService.getAllTrainingsType()).thenReturn(List.of(trainingType));
        when(trainingRestMapper.toTrainingTypeResponses(List.of(trainingType)))
                .thenReturn(List.of(response));

        List<TrainingTypeResponse> actual = facade.getAllTrainingsType();

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getName());
        verify(trainingTypeService).getAllTrainingsType();
        verify(trainingRestMapper).toTrainingTypeResponses(List.of(trainingType));
    }

    @Test
    void changePasswordShouldCallUserService() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("test.user")
                .oldPassword("old-password")
                .newPassword("new-password");

        facade.changePassword(request);

        verify(userService).changePassword(request);
    }
}