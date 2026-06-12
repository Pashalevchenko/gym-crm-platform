package gym.crm.platform.workload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WorkloadServiceTests {

    @Test
    void contextLoads() {}

    @Test
    void mainRuns() {
        WorkloadService.main(new String[]{});
    }
}
