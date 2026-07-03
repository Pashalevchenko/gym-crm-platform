package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.model.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("test")
class TrainerWorkloadRepositoryTest {

    private static final String USERNAME = "billy.herrington";
    private static final String UNKNOWN_USERNAME = "unknown.user";

    @Autowired
    private TrainerWorkloadRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void save_shouldStoreTrainerWorkload() {
        TrainerWorkload expected = buildTrainer();

        TrainerWorkload actual = repository.save(expected);

        assertEquals(USERNAME, actual.getTrainerUsername());
        assertTrue(repository.existsByTrainerUsername(USERNAME));
    }

    @Test
    void findByUsername_shouldReturnTrainerWorkload_whenExists() {
        TrainerWorkload workload = buildTrainer();
        repository.save(workload);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertTrue(actual.isPresent());
        assertEquals(USERNAME, actual.get().getTrainerUsername());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<TrainerWorkload> actual = repository.findByTrainerUsername("unknown.user");

        assertTrue(actual.isEmpty());
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        boolean actual = repository.existsByTrainerUsername(UNKNOWN_USERNAME);

        assertFalse(actual);
    }

    private TrainerWorkload buildTrainer() {
        return new TrainerWorkload(USERNAME, "Billy", "Herrington", true, new ArrayList<>());
    }
}