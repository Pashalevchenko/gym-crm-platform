package com.gym.crm.application.service;

import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Trainer getTrainerById(Long id);

    Trainer getTrainerByUsername(String username);

    List<Trainer> getAllTrainers();

    Trainer updateTrainer(Trainer trainer);

    Trainer changeActiveStatus(String username, boolean status);

    List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
}