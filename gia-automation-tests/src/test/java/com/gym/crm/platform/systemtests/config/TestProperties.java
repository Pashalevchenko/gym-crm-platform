package com.gym.crm.platform.systemtests.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestProperties {

    private static final String CORE_BASE_URL = "system.tests.core.base-url";
    private static final String WORKLOAD_BASE_URL = "system.tests.workload.base-url";
    private static final String DEFAULT_USERNAME = "system.tests.default-username";
    private static final String DEFAULT_PASSWORD = "system.tests.default-password";
    private static final String LOCAL_CORE_BASE_URL = "http://localhost:8080/gym-crm-application/api/v1";
    private static final String LOCAL_WORKLOAD_BASE_URL = "http://localhost:8080/workload-service/api/v1";
    private static final String LOCAL_USERNAME = "billy.herrington";
    private static final String LOCAL_PASSWORD = "password";

    public static String coreBaseUrl() {
        return property(CORE_BASE_URL, LOCAL_CORE_BASE_URL);
    }

    public static String workloadBaseUrl() {
        return property(WORKLOAD_BASE_URL, LOCAL_WORKLOAD_BASE_URL);
    }

    public static String defaultUsername() {
        return property(DEFAULT_USERNAME, LOCAL_USERNAME);
    }

    public static String defaultPassword() {
        return property(DEFAULT_PASSWORD, LOCAL_PASSWORD);
    }

    private static String property(String name, String defaultValue) {
        String value = System.getProperty(name);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }
}