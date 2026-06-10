package com.gym.crm.application.facade;

import com.gym.crm.application.dto.request.TraineeUpdateDTO;
import com.gym.crm.application.dto.request.TrainerUpdateDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.mapper.TraineeMapper;
import com.gym.crm.application.mapper.TrainerMapper;
import com.gym.crm.application.mapper.TrainingMapper;
import com.gym.crm.application.mapper.rest.TraineeRestMapper;
import com.gym.crm.application.mapper.rest.TrainerRestMapper;
import com.gym.crm.application.mapper.rest.TrainingRestMapper;
import com.gym.crm.application.openapi.ActivationStatusRequest;
import com.gym.crm.application.openapi.AssignedTrainerResponse;
import com.gym.crm.application.openapi.GetTraineeTrainingResponse;
import com.gym.crm.application.openapi.GetTrainerTrainingResponse;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.LoginResponse;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.application.openapi.TraineeCreateRequest;
import com.gym.crm.application.openapi.TraineeCreateResponse;
import com.gym.crm.application.openapi.TraineeGetResponse;
import com.gym.crm.application.openapi.TraineeUpdateRequest;
import com.gym.crm.application.openapi.TraineeUpdateResponse;
import com.gym.crm.application.openapi.TrainerCreateRequest;
import com.gym.crm.application.openapi.TrainerCreateResponse;
import com.gym.crm.application.openapi.TrainerGetResponse;
import com.gym.crm.application.openapi.TrainerUpdateRequest;
import com.gym.crm.application.openapi.TrainerUpdateResponse;
import com.gym.crm.application.openapi.TrainingCreateRequest;
import com.gym.crm.application.openapi.TrainingTypeResponse;
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.service.TrainingService;
import com.gym.crm.application.service.TrainingTypeService;
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.service.common.AuthenticationService;
import com.gym.crm.application.service.common.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GymAppFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final AuthenticationService authService;
    private final LogoutService logoutService;
    private final TraineeRestMapper traineeRestMapper;
    private final TrainerRestMapper trainerRestMapper;
    private final TrainingRestMapper trainingRestMapper;

    @Transactional
    public LoginResponse login(String username, String password) {
        String token = authService.authenticate(username, password);

        return new LoginResponse()
                .token(token);
    }

    public void logout(String authorizationHeader) {
        logoutService.logout(authorizationHeader);
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        Trainee trainee = traineeRestMapper.toEntity(request);
        Trainee created = traineeService.createTrainee(trainee);

        return traineeRestMapper.toCreateResponse(created);
    }

    public TraineeResponseDTO getTraineeById(Long id) {
        return traineeMapper.entityToDto(traineeService.getTraineeById(id));
    }

    @Transactional
    public TraineeGetResponse getTraineeByUsername(String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);

        return traineeRestMapper.toGetResponse(trainee);
    }

    public List<TraineeResponseDTO> getAllTrainees() {
        return traineeService.getAllTrainees().stream()
                .map(traineeMapper::entityToDto)
                .toList();
    }

    @Transactional
    public TraineeUpdateResponse updateTrainee(TraineeUpdateRequest request, String username) {
        TraineeUpdateDTO dto = traineeRestMapper.toUpdateDto(username, request);
        Trainee trainee = traineeMapper.dtoToEntity(dto);
        Trainee updated = traineeService.updateTrainee(trainee);

        return traineeRestMapper.toUpdateResponse(updated);
    }

    public void changeActiveStatus(String username, ActivationStatusRequest request) {
        traineeService.changeActiveStatus(username, request.getIsActive());
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public List<GetTraineeTrainingResponse> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        List<Training> trainings = traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);

        return traineeRestMapper.toTrainingResponses(trainings);
    }

    public List<AssignedTrainerResponse> getNotAssignedTrainers(String traineeUsername) {
        List<Trainer> trainers = traineeService.getNotAssignedTrainers(traineeUsername);

        return traineeRestMapper.toAssignedTrainerResponses(trainers);
    }

    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(String traineeUsername, TraineeAssignedTrainersUpdateRequest request) {
        Set<Trainer> trainers = request.getTrainerUsernames().stream()
                .map(trainerService::getTrainerByUsername)
                .collect(Collectors.toSet());
        Trainee updated = traineeService.updateTrainersList(traineeUsername, trainers);
        List<AssignedTrainerResponse> trainerResponses = updated.getTrainers().stream()
                .map(traineeRestMapper::toAssignedTrainerResponse)
                .toList();

        return traineeRestMapper.toAssignedTrainersUpdateResponse(trainerResponses);
    }

    public TrainerCreateResponse createTrainer(TrainerCreateRequest request) {
        TrainingType specialization = trainingTypeService.getByName(request.getSpecialization());
        Trainer trainer = trainerRestMapper.toEntity(request).toBuilder()
                .specialization(specialization)
                .build();
        Trainer created = trainerService.createTrainer(trainer);

        return trainerRestMapper.toCreateResponse(created);
    }

    @Transactional
    public TrainerGetResponse getTrainerByUsername(String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);

        return trainerRestMapper.toGetResponse(trainer);
    }

    @Transactional
    public TrainerUpdateResponse updateTrainer(TrainerUpdateRequest request, String username) {
        TrainerUpdateDTO dto = trainerRestMapper.toUpdateDto(username, request);
        Trainer trainer = trainerMapper.dtoToEntity(dto);
        Trainer updated = trainerService.updateTrainer(trainer);

        return trainerRestMapper.toUpdateResponse(updated);
    }

    public List<GetTrainerTrainingResponse> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        List<Training> trainings = trainerService.getTrainerTrainings(username, fromDate, toDate, traineeName);

        return trainerRestMapper.toTrainingResponses(trainings);
    }

    public void changeTrainerActiveStatus(String username, ActivationStatusRequest request) {
        trainerService.changeActiveStatus(username, request.getIsActive());
    }

    public void createTraining(@Valid TrainingCreateRequest request) {
        Trainee trainee = traineeService.getTraineeByUsername(request.getTraineeUsername());
        Trainer trainer = trainerService.getTrainerByUsername(request.getTrainerUsername());

        Training training = trainingRestMapper.toEntity(request).toBuilder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .build();

        trainingService.createTraining(training);
    }

    public TrainingResponseDTO getTrainingById(Long id) {
        return trainingMapper.entityToDto(trainingService.getTrainingById(id));
    }

    public List<TrainingTypeResponse> getAllTrainingsType() {
        return trainingRestMapper.toTrainingTypeResponses(trainingTypeService.getAllTrainingsType());
    }

    public void changePassword(LoginChangeRequest request){
        userService.changePassword(request);
    }
}