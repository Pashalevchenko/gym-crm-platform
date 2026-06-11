package com.gym.crm.platform.service;

import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.Training;
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