package com.gym.crm.application.service;

import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;

public interface UserService {
    void changePassword(LoginChangeRequest request);

    User findByUsername(String username);
}
