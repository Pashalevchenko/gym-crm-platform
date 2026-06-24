package com.gym.crm.platform.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.platform.actuator.metrics.MetricsService;
import com.gym.crm.platform.messaging.workload.ActionType;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.messaging.workload.TrainerWorkloadMessage;
import com.gym.crm.platform.messaging.workload.WorkloadMessageMapper;
import com.gym.crm.platform.messaging.workload.WorkloadUpdateEvent;
import com.gym.crm.platform.repository.TrainingRepository;
import com.gym.crm.platform.service.impl.TrainingServiceImpl;
import com.gym.crm.platform.validation.TrainingValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    private final Long TRAINING_ID = 1L;

    @Mock
    private TrainingRepository repository;

    @Mock
    private TrainingValidator trainingValidator;

    @Mock
    private MetricsService metrics;

    @Mock
    private WorkloadMessageMapper mapper;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private TrainingServiceImpl service;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(TrainingServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should validate and create training")
    void createTraining_shouldValidateAndCreateTraining() {
        Training training = buildTraining();
        Training createdTraining = Training.builder()
                .id(TRAINING_ID)
                .trainee(training.getTrainee())
                .trainer(training.getTrainer())
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
        TrainerWorkloadMessage workloadMessage = new TrainerWorkloadMessage("billy.herrington",
                                                                            "Billy",
                                                                            "Herrington",
                                                                                  true,
                                                                                          LocalDate.of(2026, Month.JUNE, 10),
                                                                             60,
                                                                                          ActionType.ADD);

        when(repository.save(training)).thenReturn(createdTraining);
        when(mapper.toMessage(createdTraining, ActionType.ADD)).thenReturn(workloadMessage);

        Training actual = service.createTraining(training);

        assertSame(createdTraining, actual);
        assertEquals(TRAINING_ID, actual.getId());
        assertEquals("Morning Yoga", actual.getTrainingName());
        verify(trainingValidator).validateForCreate(training);
        verify(repository).save(training);
        verify(mapper).toMessage(createdTraining, ActionType.ADD);
        verify(publisher).publishEvent(new WorkloadUpdateEvent(List.of(workloadMessage)));
        verify(metrics).incrementTrainingCreated();
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple("Training created with id: " + TRAINING_ID, Level.INFO));
    }

    @Test
    @DisplayName("Should return training when valid ID is provided")
    void getTrainingById_whenFound_shouldReturnTraining() {
        Training training = buildTrainingWithId();

        when(repository.findById(TRAINING_ID)).thenReturn(Optional.of(training));

        Training actual = service.getTrainingById(TRAINING_ID);

        assertSame(training, actual);
        assertEquals(TRAINING_ID, actual.getId());
        assertEquals("Morning Yoga", actual.getTrainingName());
        verify(repository).findById(TRAINING_ID);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when training ID does not exist")
    void getTrainingById_whenNotFound_shouldThrowException() {
        when(repository.findById(TRAINING_ID)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getTrainingById(TRAINING_ID));

        verify(repository).findById(TRAINING_ID);
    }

    @Test
    @DisplayName("Should return all trainings")
    void getAllTrainings_shouldReturnList() {
        Training firstTraining = buildTrainingWithId();
        TrainingType trainingType = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Boxing")
                .build();
        Training secondTraining = Training.builder()
                .id(2L)
                .trainingName("Evening Boxing")
                .trainingDate(LocalDate.of(2026, 4, 11))
                .trainingDuration(45)
                .trainee(Trainee.builder().id(2L).build())
                .trainer(Trainer.builder().id(2L).build())
                .trainingType(trainingType)
                .build();

        when(repository.findAll()).thenReturn(List.of(firstTraining, secondTraining));

        List<Training> actual = service.getAllTrainings();

        assertEquals(2, actual.size());
        assertEquals("Morning Yoga", actual.get(0).getTrainingName());
        assertEquals("Evening Boxing", actual.get(1).getTrainingName());
        verify(repository).findAll();
    }

    private Training buildTrainingWithId() {
        Training training = buildTraining();

        return Training.builder()
                .id(TRAINING_ID)
                .trainee(training.getTrainee())
                .trainer(training.getTrainer())
                .trainingType(training.getTrainingType())
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    private Training buildTraining() {
        return Training.builder()
                .trainee(Trainee.builder()
                        .id(10L)
                        .build())
                .trainer(Trainer.builder()
                        .id(20L)
                        .build())
                .trainingType(TrainingType.builder()
                        .id(30L)
                        .trainingTypeName("Yoga")
                        .build())
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.of(2026, 4, 10))
                .trainingDuration(60)
                .build();
    }
}