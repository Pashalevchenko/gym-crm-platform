package com.gym.crm.application.controller;

import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.ActivationStatusRequest;
import com.gym.crm.application.openapi.GetTrainerTrainingResponse;
import com.gym.crm.application.openapi.TrainerCreateRequest;
import com.gym.crm.application.openapi.TrainerCreateResponse;
import com.gym.crm.application.openapi.TrainerGetResponse;
import com.gym.crm.application.openapi.TrainerUpdateRequest;
import com.gym.crm.application.openapi.TrainerUpdateResponse;
import com.gym.crm.application.validation.annotation.ValidUsername;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api.base-path}/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final GymAppFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TrainerCreateResponse> register(@RequestBody @Valid TrainerCreateRequest request) {
        TrainerCreateResponse response = facade.createTrainer(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getTrainerProfile(@PathVariable(name = "username") @ValidUsername String username) {
        TrainerGetResponse response = facade.getTrainerByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable(name = "username") @ValidUsername String username,
                                                                      @RequestBody @Valid TrainerUpdateRequest request) {
        TrainerUpdateResponse response = facade.updateTrainer(request, username);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<GetTrainerTrainingResponse>> getTrainerTrainings(@PathVariable(name = "username") @ValidUsername String username,
                                                                                @RequestParam(name = "fromDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                                @RequestParam(name = "toDate", required = false)
                                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                                                @RequestParam(name = "traineeName", required = false) String traineeName) {
        List<GetTrainerTrainingResponse> response = facade.getTrainerTrainings(username, fromDate, toDate, traineeName);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> toggleActive(@PathVariable(name = "username") @ValidUsername String username,
                                             @RequestBody @Valid ActivationStatusRequest request) {
        facade.changeTrainerActiveStatus(username, request);

        return ResponseEntity.ok().build();
    }
}