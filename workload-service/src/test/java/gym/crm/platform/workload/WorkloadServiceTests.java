package gym.crm.platform.workload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class WorkloadServiceTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainRuns() {
        assertDoesNotThrow(() -> WorkloadService.main(new String[]{}));
    }
}
