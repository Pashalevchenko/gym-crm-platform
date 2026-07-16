package com.gym.crm.platform.systemtests.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Payloads {

    public static Map<String, Object> login(String username, String password) {
        return Map.of("username", username, "password", password);
    }

    public static Map<String, Object> trainee(String firstName, String lastName) {
        return Map.of("firstName", firstName, "lastName", lastName, "dateOfBirth", "2000-03-22", "address", "420 Oak St");
    }

    public static Map<String, Object> trainee(Map<String, String> details) {
        return Map.of("firstName", details.get("firstName"),
                      "lastName", details.get("lastName"),
                      "dateOfBirth", details.get("dateOfBirth"),
                      "address", details.get("address"));
    }

    public static Map<String, Object> trainer(String firstName, String lastName, String specialization) {
        return Map.of("firstName", firstName, "lastName", lastName, "specialization", specialization);
    }

    public static Map<String, Object> trainer(Map<String, String> details) {
        return Map.of("firstName", details.get("firstName"), "lastName", details.get("lastName"), "specialization", details.get("specialization"));
    }

    public static Map<String, Object> training(String traineeUsername, String trainerUsername, int duration) {
        return Map.of("traineeUsername", traineeUsername,
                      "trainerUsername", trainerUsername,
                      "trainingName", "System Test Training",
                      "trainingDate", LocalDate.now().toString(),
                      "trainingDuration", duration);
    }

    public static Map<String, Object> training(String traineeUsername, String trainerUsername, Map<String, String> details) {
        return Map.of("traineeUsername", traineeUsername,
                      "trainerUsername", trainerUsername,
                      "trainingName", details.get("trainingName"),
                      "trainingDate", resolveDate(details.get("trainingDate")),
                      "trainingDuration", Integer.parseInt(details.get("trainingDuration")));
    }

    public static Map<String, Object> workload(String trainerUsername, int duration) {
        return Map.of("trainerUsername", trainerUsername,
                      "trainerFirstName", "System",
                      "trainerLastName", "Trainer",
                      "isActive", true,
                      "trainingDate", LocalDate.now().toString(),
                      "trainingDuration", duration,
                      "actionType", "ADD");
    }

    public static Map<String, Object> workloadWithoutUsername(int duration) {
        return Map.of("trainerFirstName", "System",
                      "trainerLastName", "Trainer",
                      "isActive", true,
                      "trainingDate", LocalDate.now().toString(),
                      "trainingDuration", duration,
                      "actionType", "ADD");
    }

    private static String resolveDate(String date) {
        if ("today".equalsIgnoreCase(date)) {
            return LocalDate.now().toString();
        }

        return date;
    }
}