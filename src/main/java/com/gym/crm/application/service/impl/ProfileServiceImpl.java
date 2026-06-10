package com.gym.crm.application.service.impl;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.repository.TraineeRepository;
import com.gym.crm.application.repository.TrainerRepository;
import com.gym.crm.application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String createUsername(String firstName, String lastName) {
        String username = firstName + "." + lastName;
        Set<String> dbUsernames = getAllUsernames();
        int userSerialNumber = 1;

        if (!dbUsernames.contains(username)) {
            return username;
        }

        log.info("Username '{}' already exists. Starting serial number generation for {} {}", username, firstName, lastName);

        while (dbUsernames.contains(username + userSerialNumber)) {
            userSerialNumber++;
        }

        return username + userSerialNumber;
    }

    @Override
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());

            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    private Set<String> getAllUsernames() {
        return Stream.concat(traineeRepository.findAll().stream().map(Trainee::getUser).filter(Objects::nonNull).map(User::getUsername),
                        trainerRepository.findAll().stream().map(Trainer::getUser).filter(Objects::nonNull).map(User::getUsername))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}