package com.gym.crm.application.service.impl;

import com.gym.crm.application.actuator.metrics.MetricsService;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.repository.TrainerRepository;
import com.gym.crm.application.repository.TrainingRepository;
import com.gym.crm.application.repository.specification.TrainingSpecifications;
import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import com.gym.crm.application.service.ProfileService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.validation.TrainerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final ProfileService profileService;
    private final MetricsService metrics;
    private final TrainerValidator validator;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Trainer createTrainer(Trainer trainer) {
        validator.validateForCreate(trainer);

        User user = trainer.getUser();
        String username = profileService.createUsername(user.getFirstName(), user.getLastName());
        String password = profileService.generatePassword();
        User userWithCredentials = user.toBuilder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .isActive(true)
                .build();
        Trainer trainerToCreate = trainer.toBuilder()
                .user(userWithCredentials)
                .build();

        Trainer created = trainerRepository.save(trainerToCreate);

        User responseUser = created.getUser().toBuilder()
                .password(password)
                .build();

        metrics.incrementTrainerCreated();
        log.info("Trainer profile created with username: {}", username);
        return created.toBuilder()
                .user(responseUser)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id).orElseThrow(() -> new NoSuchElementException(String.format("Trainer with ID %d not found", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public Trainer getTrainerByUsername(String username) {
        validator.validateUsername(username);

        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new NoSuchElementException(String.format("Trainer with username %s  not found", username)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    @Transactional
    @Override
    public Trainer updateTrainer(Trainer trainer) {
        validator.validateForUpdate(trainer);

        Trainer existing = getTrainerByUsername(trainer.getUser().getUsername());
        User userToUpdate = existing.getUser().toBuilder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .build();
        Trainer trainerToUpdate = existing.toBuilder()
                .specialization(trainer.getSpecialization())
                .user(userToUpdate)
                .build();
        Trainer updated = trainerRepository.save(trainerToUpdate);

        log.info("Trainer profile updated for username: {}", updated.getUser().getUsername());
        return updated;
    }

    @Transactional
    @Override
    public Trainer changeActiveStatus(String username, boolean status) {
        Trainer trainer = getTrainerByUsername(username);
        boolean currentStatus = trainer.getUser().isActive();

        if (currentStatus == status) {
            String errorMessage = status ? "Trainer is already active" : "Trainer is already inactive";

            throw new IllegalStateException(errorMessage);
        }

        return updateActiveStatus(trainer, status);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        validator.validateUsername(username);

        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();
        Specification<Training> specification = TrainingSpecifications.byTrainerCriteria(filter);

        return trainingRepository.findAll(specification);
    }

    private Trainer updateActiveStatus(Trainer trainer, boolean active) {
        User userToUpdate = trainer.getUser().toBuilder()
                .isActive(active)
                .build();
        Trainer trainerToUpdate = trainer.toBuilder()
                .user(userToUpdate)
                .build();

        return trainerRepository.save(trainerToUpdate);
    }
}