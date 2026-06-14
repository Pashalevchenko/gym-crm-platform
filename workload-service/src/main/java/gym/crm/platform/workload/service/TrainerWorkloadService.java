package gym.crm.platform.workload.service;

import gym.crm.platform.workload.model.MonthSummary;
import gym.crm.platform.workload.model.TrainerWorkload;
import gym.crm.platform.workload.model.YearSummary;
import gym.crm.platform.workload.openapi.TrainerWorkloadRequest;
import gym.crm.platform.workload.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    public void updateTrainerWorkload(TrainerWorkloadRequest request) {
        TrainerWorkload workload = trainerWorkloadRepository.findByUsername(request.getTrainerUsername())
                .map(existing -> refreshTrainer(existing, request))
                .orElseGet(() -> createWorkload(request));

        updateMonthlySummary(workload, request);
        trainerWorkloadRepository.save(workload);

        log.info("Trainer workload updated. username={}, actionType={}, date={}, duration={}",
                request.getTrainerUsername(),
                request.getActionType(),
                request.getTrainingDate(),
                request.getTrainingDuration());
    }

    public int getMonthlyWorkload(String username, int year, int month) {
        TrainerWorkload workload = findWorkload(username);

        return workload.getYears().stream()
                .filter(summary -> summary.getYear().equals(year))
                .flatMap(summary -> summary.getMonths().stream())
                .filter(summary -> summary.getMonth().equals(month))
                .mapToInt(MonthSummary::getTrainingSummaryDuration)
                .findFirst()
                .orElse(0);
    }

    private TrainerWorkload findWorkload(String username) {
        return trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainer workload not found: " + username));
    }

    private TrainerWorkload createWorkload(TrainerWorkloadRequest request) {
        return new TrainerWorkload(request.getTrainerUsername(),
                request.getTrainerFirstName(),
                request.getTrainerLastName(),
                request.getIsActive(),
                new ArrayList<>()
        );
    }

    private TrainerWorkload refreshTrainer(TrainerWorkload workload, TrainerWorkloadRequest request) {
        workload.setTrainerFirstName(request.getTrainerFirstName());
        workload.setTrainerLastName(request.getTrainerLastName());
        workload.setIsActive(request.getIsActive());

        return workload;
    }

    private void updateMonthlySummary(TrainerWorkload workload, TrainerWorkloadRequest request) {
        LocalDate date = request.getTrainingDate();

        YearSummary yearSummary = getOrCreateYearSummary(workload.getYears(), date.getYear());
        MonthSummary monthSummary = getOrCreateMonthSummary(yearSummary.getMonths(), date.getMonthValue());

        int delta = switch (request.getActionType()) {
            case ADD -> request.getTrainingDuration();
            case DELETE -> -request.getTrainingDuration();
        };

        int updatedDuration = Math.max(0, monthSummary.getTrainingSummaryDuration() + delta);
        monthSummary.setTrainingSummaryDuration(updatedDuration);
    }

    private YearSummary getOrCreateYearSummary(List<YearSummary> years, int year) {
        return years.stream()
                .filter(summary -> summary.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    YearSummary summary = new YearSummary(year, new ArrayList<>());
                    years.add(summary);
                    return summary;
                });
    }

    private MonthSummary getOrCreateMonthSummary(List<MonthSummary> months, int month) {
        return months.stream()
                .filter(summary -> summary.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary summary = new MonthSummary(month, 0);
                    months.add(summary);
                    return summary;
                });
    }
}