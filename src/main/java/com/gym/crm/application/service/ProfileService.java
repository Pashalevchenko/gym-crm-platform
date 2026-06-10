package com.gym.crm.application.service;

public interface ProfileService {

    String createUsername(String firstName, String lastname);

    String generatePassword();
}
