package com.gym.crm.application.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityContextHolderTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clear();
    }

    @Test
    @DisplayName("Should successfully set and get username in the same thread")
    void setAndGetContext_shouldWorkInSameThread() {
        String username = "ivan.ivanov";
        SecurityContextHolder.setContext(username);

        assertEquals(username, SecurityContextHolder.getContext());
    }

    @Test
    @DisplayName("Should return null after clearing the context")
    void clear_shouldRemoveUsername() {
        SecurityContextHolder.setContext("some.user");
        SecurityContextHolder.clear();

        assertNull(SecurityContextHolder.getContext());
    }

    @Test
    @DisplayName("Should ensure thread isolation for different users")
    void context_shouldBeThreadLocalAndIsolated() throws InterruptedException {
        String mainThreadUser = "main.user";
        String secondThreadUser = "second.user";
        AtomicReference<String> capturedUserInThread = new AtomicReference<>();

        SecurityContextHolder.setContext(mainThreadUser);

        Thread secondThread = new Thread(() -> {
            SecurityContextHolder.setContext(secondThreadUser);
            capturedUserInThread.set(SecurityContextHolder.getContext());
            SecurityContextHolder.clear();
        });

        secondThread.start();
        secondThread.join();

        assertEquals(secondThreadUser, capturedUserInThread.get());
        assertEquals(mainThreadUser, SecurityContextHolder.getContext());
    }
}