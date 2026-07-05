package gym.crm.platform.workload.repository;

import gym.crm.platform.workload.config.MongoContainerTestConfig;
import gym.crm.platform.workload.model.MonthSummary;
import gym.crm.platform.workload.model.TrainerWorkload;
import gym.crm.platform.workload.model.YearSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class TrainerWorkloadRepositoryTest {

    private static final String USERNAME = "billy.herrington";
    private static final String UNKNOWN_USERNAME = "unknown.user";
    private static final String FIRST_NAME = "Billy";
    private static final String LAST_NAME = "Herrington";
    private static final int YEAR = 2026;
    private static final int MONTH = 6;
    private static final int DURATION = 60;

    @Autowired
    private TrainerWorkloadRepository repository;

    @Autowired
    private MongoTemplate template;

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        MongoContainerTestConfig.setMongoContainerProperties(registry);
    }

    @BeforeEach
    void setUp() {
        template.dropCollection(TrainerWorkload.class);
    }

    @Test
    void save_shouldStoreTrainerWorkload() {
        TrainerWorkload expected = buildTrainerWorkload();
        TrainerWorkload saved = repository.save(expected);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertThat(saved.getTrainerUsername()).isEqualTo(USERNAME);
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .isEqualTo(saved);
    }

    @Test
    void findByTrainerUsername_shouldReturnTrainerWorkload_whenExists() {
        TrainerWorkload expected = buildTrainerWorkload();
        template.save(expected);

        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void findByTrainerUsername_shouldReturnEmpty_whenNotExists() {
        Optional<TrainerWorkload> actual = repository.findByTrainerUsername(UNKNOWN_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void existsByTrainerUsername_shouldReturnTrue_whenExists() {
        TrainerWorkload expected = buildTrainerWorkload();
        template.save(expected);

        boolean actual = repository.existsByTrainerUsername(USERNAME);

        assertThat(actual).isTrue();
    }

    @Test
    void existsByTrainerUsername_shouldReturnFalse_whenNotExists() {
        boolean actual = repository.existsByTrainerUsername(UNKNOWN_USERNAME);

        assertThat(actual).isFalse();
    }

    private TrainerWorkload buildTrainerWorkload() {
        MonthSummary monthSummary = new MonthSummary(MONTH, DURATION);
        YearSummary yearSummary = new YearSummary(YEAR, List.of(monthSummary));

        return new TrainerWorkload(USERNAME, FIRST_NAME, LAST_NAME, true, List.of(yearSummary));
    }
}