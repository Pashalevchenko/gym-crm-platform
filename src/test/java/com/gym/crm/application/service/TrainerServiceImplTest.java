package com.gym.crm.application.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.actuator.metrics.MetricsService;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.repository.TrainerRepository;
import com.gym.crm.application.service.impl.TrainerServiceImpl;
import com.gym.crm.application.validation.TrainerValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    private final Long TRAINER_ID = 1L;
    private final Long USER_ID = 10L;
    private final Long TRAINING_TYPE_ID = 100L;
    private final String FIRST_NAME = "Ivan";
    private final String LAST_NAME = "Ivanov";
    private final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private final String PASSWORD = "secure123";

    @Mock
    private TrainerRepository repository;

    @Mock
    private ProfileService profileService;

    @Mock
    private TrainerValidator trainerValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MetricsService metrics;

    @InjectMocks
    private TrainerServiceImpl service;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(TrainerServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should create trainer with generated username, password and specialization")
    void createTrainer_shouldCreateTrainerWithGeneratedCredentials() {
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(buildTrainingType())
                .build();

        String encodedPassword = "encodedPassword123";

        when(profileService.createUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(profileService.generatePassword()).thenReturn(PASSWORD);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(encodedPassword);

        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.createTrainer(trainer);

        assertNotNull(actual);
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals("Yoga", actual.getSpecialization().getTrainingTypeName());
        verify(trainerValidator).validateForCreate(trainer);
        verify(profileService).createUsername(FIRST_NAME, LAST_NAME);
        verify(profileService).generatePassword();
        verify(repository).save(any(Trainer.class));
        verify(metrics).incrementTrainerCreated();

    }

    @Test
    @DisplayName("Should return trainer profile when a valid ID is provided")
    void getTrainerById_WhenFound() {
        Trainer trainer = buildTrainer(true);

        when(repository.findById(TRAINER_ID)).thenReturn(Optional.of(trainer));

        Trainer actual = service.getTrainerById(TRAINER_ID);

        assertEquals(TRAINER_ID, actual.getId());
        assertEquals(USERNAME, actual.getUser().getUsername());
        verify(repository).findById(TRAINER_ID);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when trainer ID does not exist")
    void getTrainerById_WhenNotFound() {
        when(repository.findById(TRAINER_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getTrainerById(TRAINER_ID));
        verify(repository).findById(TRAINER_ID);
    }

    @Test
    @DisplayName("Should return trainer by username")
    void getTrainerByUsername_whenFound_shouldReturnTrainer() {
        Trainer trainer = buildTrainer(true);

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(trainer));

        Trainer actual = service.getTrainerByUsername(USERNAME);

        assertEquals(USERNAME, actual.getUser().getUsername());
        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when username does not exist")
    void getTrainerByUsername_whenNotFound_shouldThrowException() {
        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getTrainerByUsername(USERNAME));

        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return all trainers")
    void getAllTrainers_shouldReturnList() {
        List<Trainer> trainers = List.of(buildTrainer(true), buildTrainer(false));

        when(repository.findAll()).thenReturn(trainers);

        List<Trainer> actual = service.getAllTrainers();

        assertEquals(2, actual.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should update trainer while preserving username, password and active status")
    void updateTrainer_shouldUpdateProfileAndPreserveCredentials() {
        Trainer existing = buildTrainer(true);
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        TrainingType trainingType = TrainingType.builder()
                .id(200L)
                .trainingTypeName("Boxing")
                .build();
        Trainer updateRequest = Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .specialization(trainingType)
                .build();

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.updateTrainer(updateRequest);

        assertEquals(TRAINER_ID, actual.getId());
        assertEquals(USER_ID, actual.getUser().getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
        verify(trainerValidator).validateForUpdate(updateRequest);
        verify(repository).findByUserUsername(USERNAME);
        verify(repository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should activate inactive trainer")
    void changeActiveStatus_whenInactiveAndStatusTrue_shouldActivate() {
        Trainer inactiveTrainer = buildTrainer(false);

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(inactiveTrainer));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.changeActiveStatus(USERNAME, true);

        assertTrue(actual.getUser().isActive());
        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
        verify(repository).save(argThat(updatedTrainer ->
                updatedTrainer.getUser().isActive() && updatedTrainer.getUser().getUsername().equals(USERNAME)));
    }

    @Test
    @DisplayName("Should deactivate active trainer")
    void changeActiveStatus_whenActiveAndStatusFalse_shouldDeactivate() {
        Trainer activeTrainer = buildTrainer(true);

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(activeTrainer));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer actual = service.changeActiveStatus(USERNAME, false);

        assertFalse(actual.getUser().isActive());
        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
        verify(repository).save(argThat(updatedTrainer ->
                !updatedTrainer.getUser().isActive() && updatedTrainer.getUser().getUsername().equals(USERNAME)));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trainer is already active")
    void changeActiveStatus_whenAlreadyActive_shouldThrowException() {
        Trainer activeTrainer = buildTrainer(true);

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(activeTrainer));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.changeActiveStatus(USERNAME, true));

        assertEquals("Trainer is already active", exception.getMessage());
        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trainer is already inactive")
    void changeActiveStatus_whenAlreadyInactive_shouldThrowException() {
        Trainer inactiveTrainer = buildTrainer(false);

        when(repository.findByUserUsername(USERNAME)).thenReturn(Optional.of(inactiveTrainer));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.changeActiveStatus(USERNAME, false));

        assertEquals("Trainer is already inactive", exception.getMessage());
        verify(trainerValidator).validateUsername(USERNAME);
        verify(repository).findByUserUsername(USERNAME);
        verify(repository, never()).save(any(Trainer.class));
    }

    private Trainer buildTrainer(boolean active) {
        User user = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(active)
                .build();

        return Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .specialization(buildTrainingType())
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(TRAINING_TYPE_ID)
                .trainingTypeName("Yoga")
                .build();
    }
}