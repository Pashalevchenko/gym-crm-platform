package com.gym.crm.platform.service;

import com.gym.crm.platform.entity.User;
import com.gym.crm.platform.openapi.LoginChangeRequest;

public interface UserService {
    void changePassword(LoginChangeRequest request);

    User findByUsername(String username);
}
