package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {

    Optional<TrainerWorkload> findByTrainerUsername(String username);

    boolean existsByTrainerUsername(String username);
}