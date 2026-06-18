package com.gym.crm.platform.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.platform.actuator.metrics.MetricsService;
import com.gym.crm.platform.client.workload.WorkloadRequestMapper;
import com.gym.crm.platform.client.workload.WorkloadUpdateEvent;
import com.gym.crm.platform.client.workload.model.ActionType;
import com.gym.crm.platform.client.workload.model.TrainerWorkloadRequest;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.User;
import com.gym.crm.platform.repository.TraineeRepository;
import com.gym.crm.platform.repository.TrainerRepository;
import com.gym.crm.platform.service.impl.TraineeServiceImpl;
import com.gym.crm.platform.validation.TraineeValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    private final Long TRAINEE_ID = 1L;
    private final Long USER_ID = 10L;
    private final String FIRST_NAME = "Ivan";
    private final String LAST_NAME = "Ivanov";
    private final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private final String PASSWORD = "randomPass123";

    @Mock
    private TraineeRepository repository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private TraineeValidator traineeValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsService metrics;

    @Mock
    private WorkloadRequestMapper requestMapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private TraineeServiceImpl service;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should create trainee with generated username and encoded password")
    void createTrainee_shouldCreateTraineeWithGeneratedCredentials() {
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();

        String rawPassword = "raw_password_123";
        String encodedHash = "hashed_content";

        when(profileService.createUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(profileService.generatePassword()).thenReturn(rawPassword);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedHash);
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = service.createTrainee(trainee);

        assertNotNull(actual);
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertNotNull(actual.getUser());
        assertEquals(rawPassword, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        verify(traineeValidator).validateForCreate(trainee);
        verify(profileService).createUsername(FIRST_NAME, LAST_NAME);
        verify(profileService).generatePassword();
        verify(passwordEncoder).encode(rawPassword);
        verify(repository).save(any(Trainee.class));
        verify(metrics).incrementTraineeCreated();
    }

    @Test
    @DisplayName("Should return trainee when valid ID is provided")
    void getTraineeById_whenFound_shouldReturnTrainee() {
        Trainee trainee = buildTrainee(true);

        when(repository.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        Trainee actual = service.getTraineeById(TRAINEE_ID);

        assertEquals(TRAINEE_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        verify(repository).findById(TRAINEE_ID);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when trainee ID does not exist")
    void getTraineeById_whenNotFound() {
        Long id = 99L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getTraineeById(id));
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Should update trainee profile and preserve username, password and active status")
    void updateTrainee_shouldUpdateProfileAndPreserveCredentials() {
        Trainee existing = buildTrainee(true);
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = service.updateTrainee(updateRequest);

        assertEquals(TRAINEE_ID, actual.getId());
        assertEquals(USER_ID, actual.getUser().getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals(LocalDate.of(1999, 5, 10), actual.getDateOfBirth());
        assertEquals("Lviv", actual.getAddress());
        verify(traineeValidator).validateForUpdate(updateRequest);
        verify(repository).findByUserUsername(USERNAME);
        verify(repository).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Should pass correctly rebuilt trainee to DAO during update")
    void updateTrainee_shouldPassCorrectTraineeToDao() {
        Trainee existing = buildTrainee(true);
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateTrainee(updateRequest);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(repository).save(captor.capture());

        Trainee passedToDao = captor.getValue();

        assertEquals(TRAINEE_ID, passedToDao.getId());
        assertEquals(USER_ID, passedToDao.getUser().getId());
        assertEquals(FIRST_NAME, passedToDao.getUser().getFirstName());
        assertEquals(LAST_NAME, passedToDao.getUser().getLastName());
        assertEquals(USERNAME, passedToDao.getUser().getUsername());
        assertEquals(USERNAME, passedToDao.getUser().getUsername());
        assertEquals(PASSWORD, passedToDao.getUser().getPassword());
        assertTrue(passedToDao.getUser().isActive());
        assertEquals(LocalDate.of(1999, 5, 10), passedToDao.getDateOfBirth());
        assertEquals("Lviv", passedToDao.getAddress());
    }

    @Test
    @DisplayName("Should return all trainees")
    void getAllTrainees_shouldReturnList() {
        User user = User.builder()
                .id(20L)
                .firstName("Anna")
                .lastName("Smith")
                .username("anna.smith")
                .password("pass")
                .isActive(true)
                .build();
        List<Trainee> trainees = List.of(buildTrainee(true),
                Trainee.builder()
                        .id(2L)
                        .user(user)
                        .build()
        );

        when(repository.findAll()).thenReturn(trainees);

        List<Trainee> actual = service.getAllTrainees();

        assertEquals(2, actual.size());
        assertEquals(USERNAME, actual.get(0).getUser().getUsername());
        assertEquals("anna.smith", actual.get(1).getUser().getUsername());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_shouldValidateUsernameAndDelete() {
        String username = "test.user";
        User trainerUser = User.builder()
                .username("trainer.user")
                .firstName("Trainer")
                .lastName("User")
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .build();
        Training training = Training.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.of(2026, Month.JUNE, 10))
                .trainingDuration(60)
                .build();
        Trainee trainee = Trainee.builder()
                .user(User.builder().username(username).build())
                .trainings(Set.of(training))
                .build();
        TrainerWorkloadRequest workloadRequest = new TrainerWorkloadRequest()
                .trainerUsername("trainer.user");

        when(repository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(requestMapper.toRequest(training, ActionType.DELETE)).thenReturn(workloadRequest);

        service.deleteTraineeByUsername(username);

        verify(repository).findByUserUsername(username);
        verify(requestMapper).toRequest(training, ActionType.DELETE);
        verify(repository).deleteByUserUsername(username);
        verify(publisher).publishEvent(new WorkloadUpdateEvent(List.of(workloadRequest)));
    }

    @Test
    @DisplayName("Should get not assigned trainers")
    void getNotAssignedTrainers_shouldValidateUsernameAndReturnTrainers() {
        String username = "test.user";
        Trainer trainer = Trainer.builder()
                .id(1L)
                .build();

        when(repository.findNotAssignedTrainers(username)).thenReturn(List.of(trainer));

        List<Trainer> actual = service.getNotAssignedTrainers(username);

        assertEquals(1, actual.size());
        assertEquals(trainer, actual.get(0));
        verify(traineeValidator).validateUsername(username);
        verify(repository).findNotAssignedTrainers(username);
    }

    @Test
    @DisplayName("Should update trainee trainers list")
    void updateTrainersList_shouldValidateAndUpdateTrainersList() {
        String traineeUsername = "test.user";
        String trainerUsername = "trainer.user";
        User trainerUser = User.builder()
                .username(trainerUsername)
                .build();
        User traineeUser = User.builder()
                .username(trainerUsername)
                .build();
        Trainer inputTrainer = Trainer.builder()
                .id(1L)
                .user(trainerUser)
                .build();
        Trainer managedTrainer = Trainer.builder()
                .id(1L)
                .user(traineeUser)
                .build();
        Set<Trainer> inputTrainers = Set.of(inputTrainer);
        Set<Trainer> managedTrainers = Set.of(managedTrainer);
        User existingTraineeUser = User.builder()
                .username(traineeUsername)
                .build();
        Trainee existingTrainee = Trainee.builder()
                .id(1L)
                .user(existingTraineeUser)
                .trainers(new HashSet<>())
                .build();
        Trainee updatedTrainee = existingTrainee.toBuilder()
                .trainers(managedTrainers)
                .build();

        when(repository.findByUserUsername(traineeUsername)).thenReturn(Optional.of(existingTrainee));
        when(trainerRepository.findByUserUsername(trainerUsername)).thenReturn(Optional.of(managedTrainer));
        when(repository.save(any(Trainee.class))).thenReturn(updatedTrainee);

        Trainee actual = service.updateTrainersList(traineeUsername, inputTrainers);

        assertEquals(updatedTrainee, actual);
        verify(traineeValidator).validateTrainersList(inputTrainers);
        verify(repository).findByUserUsername(traineeUsername);
        verify(trainerRepository).findByUserUsername(trainerUsername);
        verify(repository).save(argThat(trainee -> trainee.getId().equals(1L) && trainee.getTrainers().contains(managedTrainer)));
    }

    @Test
    @DisplayName("Should activate inactive trainee")
    void changeActiveStatus_shouldActivateInactiveTrainee() {
        User user = User.builder()
                .username(USERNAME)
                .isActive(false)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .build();

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = service.changeActiveStatus(USERNAME, true);

        assertTrue(actual.getUser().isActive());
        verify(repository).findByUserUsername(USERNAME);
        verify(repository).save(argThat(updated -> updated.getUser().isActive() && updated.getUser().getUsername().equals(USERNAME)));
    }

    private Trainee buildTrainee(boolean active) {
        User user = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(active)
                .build();

        return Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();
    }
}