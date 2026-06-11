package com.gym.crm.platform.service;

public interface ProfileService {

    String createUsername(String firstName, String lastname);

    String generatePassword();
}
