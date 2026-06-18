package com.gym.crm.platform.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class DiscoveryServerTest {

    @Test
    void contextLoads() {
    }

    @Test
    void mainRuns() {
        assertDoesNotThrow(() -> DiscoveryServer.main(new String[]{}));
    }
}
