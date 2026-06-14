package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.model.TrainerWorkload;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadRepositoryTest {

    private static final String USERNAME = "billy.herrington";

    private final TrainerWorkloadRepositoryImpl repository = new TrainerWorkloadRepositoryImpl();

    @Test
    void save_shouldStoreTrainerWorkload() {
        TrainerWorkload workload = buildTrainer();

        TrainerWorkload saved = repository.save(workload);

        assertSame(workload, saved);
        assertTrue(repository.existsByUsername(USERNAME));
    }

    @Test
    void findByUsername_shouldReturnTrainerWorkload_whenExists() {
        TrainerWorkload workload = buildTrainer();

        repository.save(workload);

        Optional<TrainerWorkload> result = repository.findByUsername(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getTrainerUsername());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<TrainerWorkload> result = repository.findByUsername("unknown.user");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        assertFalse(repository.existsByUsername("unknown.user"));
    }

    private TrainerWorkload buildTrainer(){
        return new TrainerWorkload(USERNAME, "Billy", "Herrington", true, new ArrayList<>());
    }
}