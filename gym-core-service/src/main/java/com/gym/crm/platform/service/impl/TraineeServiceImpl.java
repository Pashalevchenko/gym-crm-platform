package com.gym.crm.platform.service.impl;

import com.gym.crm.platform.actuator.metrics.MetricsService;
import com.gym.crm.platform.messaging.workload.ActionType;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
import com.gym.crm.platform.entity.User;
import com.gym.crm.platform.messaging.workload.TrainerWorkloadMessage;
import com.gym.crm.platform.messaging.workload.WorkloadMessageMapper;
import com.gym.crm.platform.messaging.workload.WorkloadUpdateEvent;
import com.gym.crm.platform.repository.TraineeRepository;
import com.gym.crm.platform.repository.TrainerRepository;
import com.gym.crm.platform.repository.TrainingRepository;
import com.gym.crm.platform.repository.specification.TrainingSpecifications;
import com.gym.crm.platform.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.platform.service.ProfileService;
import com.gym.crm.platform.service.TraineeService;
import com.gym.crm.platform.validation.TraineeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final ProfileService profileService;
    private final MetricsService metrics;
    private final TraineeValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final WorkloadMessageMapper mapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Override
    public Trainee createTrainee(Trainee trainee) {
        validator.validateForCreate(trainee);

        User user = trainee.getUser();
        String username = profileService.createUsername(user.getFirstName(), user.getLastName());
        String password = profileService.generatePassword();

        User userWithCredentials = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(username)
                .password(passwordEncoder.encode(password))
                .isActive(true)
                .build();
        Trainee traineeToCreate = Trainee.builder()
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userWithCredentials)
                .build();

        Trainee created = traineeRepository.save(traineeToCreate);

        User responseUser = created.getUser().toBuilder()
                .password(password)
                .build();

        metrics.incrementTraineeCreated();

        return created.toBuilder()
                .user(responseUser)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Trainee getTraineeById(Long id) {
        return traineeRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainee with ID %d not found", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public Trainee getTraineeByUsername(String username) {
        validator.validateUsername(username);

        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new NoSuchElementException(String.format("Trainee with username %s not found", username)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }

    @Transactional
    @Override
    public Trainee updateTrainee(Trainee trainee) {
        validator.validateForUpdate(trainee);

        Trainee existing = getTraineeByUsername(trainee.getUser().getUsername());
        User userToUpdate = existing.getUser().toBuilder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .build();
        Trainee traineeToUpdate = existing.toBuilder()
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userToUpdate)
                .build();

        return traineeRepository.save(traineeToUpdate);
    }

    @Transactional
    @Override
    public Trainee changeActiveStatus(String username, boolean status){
        Trainee trainee = getTraineeByUsername(username);
        boolean currentStatus = trainee.getUser().isActive();

        if (currentStatus == status) {
            String errorMessage = status ? "Trainee is already active" : "Trainee is already inactive";

            throw new IllegalStateException(errorMessage);
        }

        return updateActiveStatus(trainee, status);
    }

    @Transactional
    @Override
    public void deleteTraineeByUsername(String username) {
        validator.validateUsername(username);

        Trainee trainee = traineeRepository.findByUserUsername(username).orElseThrow(() -> new NoSuchElementException(String.format("Trainee with username %s not found", username)));
        List<TrainerWorkloadMessage> messages = trainee.getTrainings().stream()
                .map(training -> mapper.toMessage(training, ActionType.DELETE))
                .toList();

        traineeRepository.deleteByUserUsername(username);

        publisher.publishEvent(new WorkloadUpdateEvent(messages));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        validator.validateUsername(username);

        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingTypeName(trainingTypeName)
                .build();

        return trainingRepository.findAll(TrainingSpecifications.byTraineeCriteria(filter));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trainer> getNotAssignedTrainers(String traineeUsername) {
        validator.validateUsername(traineeUsername);

        return traineeRepository.findNotAssignedTrainers(traineeUsername);
    }

    @Transactional
    @Override
    public Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers) {
        validator.validateUsername(traineeUsername);
        validator.validateTrainersList(trainers);

        Trainee trainee = getTraineeByUsername(traineeUsername);

        Set<Trainer> managedTrainers = trainers.stream()
                .map(trainer -> trainerRepository.findByUserUsername(trainer.getUser().getUsername())
                        .orElseThrow(() -> new NoSuchElementException(String.format("Trainer with username %s not found",
                                trainer.getUser().getUsername()))))
                .collect(Collectors.toSet());

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(managedTrainers);

        Trainee updated = traineeRepository.save(trainee);

        log.info("Trainers list updated for trainee username: {}", traineeUsername);
        return updated;
    }

    private Trainee updateActiveStatus(Trainee trainee, boolean active) {
        User updatedUser = trainee.getUser().toBuilder()
                .isActive(active)
                .build();
        Trainee traineeToUpdate = trainee.toBuilder()
                .user(updatedUser)
                .build();

        return traineeRepository.save(traineeToUpdate);
    }
}