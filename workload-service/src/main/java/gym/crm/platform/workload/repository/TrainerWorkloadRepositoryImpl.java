package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.model.TrainerWorkload;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainerWorkloadRepositoryImpl implements TrainerWorkloadRepository{

    private final Map<String, TrainerWorkload> trainerWorkloads = new ConcurrentHashMap<>();

    public Optional<TrainerWorkload> findByUsername(String username) {
        return Optional.ofNullable(trainerWorkloads.get(username));
    }

    public TrainerWorkload save(TrainerWorkload trainerWorkload) {
        trainerWorkloads.put(trainerWorkload.getTrainerUsername(), trainerWorkload);

        return trainerWorkload;
    }

    public boolean existsByUsername(String username) {
        return trainerWorkloads.containsKey(username);
    }
}
