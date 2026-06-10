package com.gym.crm.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.repository.TraineeRepository;
import com.gym.crm.application.repository.TrainerRepository;
import com.gym.crm.application.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final String USERNAME_PLUS_ONE = USERNAME + "1";

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        lenient().when(traineeRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        logger = (Logger) LoggerFactory.getLogger(ProfileServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should successfully generate a standard username by joining first and last name with a dot")
    void createUsername_simpleCase() {
        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME, actual);
    }

    @Test
    @DisplayName("Should append an index to the username when a collision with an existing trainee occurs")
    void createUsername_withTraineeCollision() {
        User user = User.builder()
                .username(USERNAME)
                .build();
        Trainee existingTrainee = Trainee.builder()
                .user(user)
                .build();

        when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME_PLUS_ONE, actual);
    }

    @Test
    @DisplayName("Should increment username suffix correctly when multiple collisions exist across trainee and trainer records")
    void createUsername_multipleCollisions() {
        User user = User.builder()
                .username(USERNAME)
                .build();
        Trainee existingTrainee = Trainee.builder()
                .user(user)
                .build();
        User UserPlusOne = User.builder()
                .username(USERNAME_PLUS_ONE)
                .build();
        Trainer existingTrainer = Trainer.builder()
                .user(UserPlusOne)
                .build();

        when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));
        when(trainerRepository.findAll()).thenReturn(List.of(existingTrainer));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME + 2, actual);
    }

    @Test
    @DisplayName("Should generate a 10-character password")
    void generatePassword_shouldGeneratePasswordWithExpectedLength() {
        String password = profileService.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    @DisplayName("Should generate password only from allowed characters")
    void generatePassword_shouldUseOnlyAllowedCharacters() {
        String password = profileService.generatePassword();

        assertThat(password).matches("[A-Za-z0-9]{10}");
    }

    @Test
    @DisplayName("Should log INFO message when a duplicate username is detected during generation")
    void createUsername_shouldLogWhenDuplicateFound() {
        User user = User.builder()
                .username(USERNAME)
                .build();
        Trainee existingTrainee = Trainee.builder()
                .user(user)
                .build();

        when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

        profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple("Username 'Ivan.Ivanov' already exists. Starting serial number generation for Ivan Ivanov", Level.INFO));
    }

    @Test
    @DisplayName("Should ignore users with null profile user")
    void createUsername_shouldIgnoreNullUserInsideProfile() {
        Trainee traineeWithoutUser = Trainee.builder()
                .user(null)
                .build();

        when(traineeRepository.findAll()).thenReturn(List.of(traineeWithoutUser));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME, actual);
    }

    @Test
    @DisplayName("Should ignore null usernames")
    void createUsername_shouldIgnoreNullUsername() {
        User user = User.builder()
                .username(null)
                .build();
        Trainee traineeWithNullUsername = Trainee.builder()
                .user(user)
                .build();

        when(traineeRepository.findAll()).thenReturn(List.of(traineeWithNullUsername));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME, actual);
    }
}