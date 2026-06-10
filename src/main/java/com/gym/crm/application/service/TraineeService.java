package com.gym.crm.application.service;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    Trainee getTraineeById(Long id);

    Trainee getTraineeByUsername(String username);

    List<Trainee> getAllTrainees();

    Trainee updateTrainee(Trainee trainee);

    void deleteTraineeByUsername(String username);

    List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName);

    List<Trainer> getNotAssignedTrainers(String traineeUsername);

    Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers);

    Trainee changeActiveStatus(String username, boolean status);
}