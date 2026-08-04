package gym.crm.platform.workload;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import static org.mockito.Mockito.mockStatic;

class WorkloadServiceTests {

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            WorkloadService.main(args);

            springApplication.verify(() -> SpringApplication.run(WorkloadService.class, args));
        }
    }
}
