package com.gym.crm.platform.controller;

import com.gym.crm.platform.facade.GymAppFacade;
import com.gym.crm.platform.openapi.TrainingCreateRequest;
import com.gym.crm.platform.openapi.TrainingTypeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("${app.api.base-path}/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final GymAppFacade facade;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid TrainingCreateRequest request){
        facade.createTraining(request);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes(){
        List<TrainingTypeResponse> response = facade.getAllTrainingsType();

        return ResponseEntity.ok(response);
    }
}