package com.gym.crm.application.service.impl;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.repository.UserRepository;
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.service.common.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final AuthenticationService authentication;

    @Transactional
    @Override
    public void changePassword(LoginChangeRequest request) {
        User existingUser = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        authentication.verifyPassword(request.getOldPassword(), existingUser.getPassword(), "The provided old password does not match the current password");

        User userToUpdate = existingUser.toBuilder()
                .password(authentication.encodePassword(request.getNewPassword()))
                .build();

        repository.save(userToUpdate);
        log.info("Password changed for username: {}", request.getUsername());
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
