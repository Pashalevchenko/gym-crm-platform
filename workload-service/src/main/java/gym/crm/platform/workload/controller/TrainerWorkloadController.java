package gym.crm.platform.workload.controller;

import gym.crm.platform.workload.openapi.TrainerWorkloadRequest;
import gym.crm.platform.workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainer-workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;

    @PutMapping
    public ResponseEntity<Void> updateTrainerWorkload(@Valid @RequestBody TrainerWorkloadRequest request) {
        trainerWorkloadService.updateTrainerWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Integer> getTrainerMonthlyWorkload(@PathVariable String username, @RequestParam int year, @RequestParam int month) {
        int monthlyWorkload = trainerWorkloadService.getMonthlyWorkload(username, year, month);
        return ResponseEntity.ok(monthlyWorkload);
    }
}