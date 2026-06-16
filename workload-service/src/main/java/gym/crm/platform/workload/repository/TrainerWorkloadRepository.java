package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.model.TrainerWorkload;

import java.util.Optional;

public interface TrainerWorkloadRepository {

    Optional<TrainerWorkload> findByUsername(String username);

    TrainerWorkload save(TrainerWorkload trainerWorkload);

    boolean existsByUsername(String username);
}