package com.gym.crm.application.search.filter;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class TrainingSearchFilter {
    private final String username;
    private final LocalDate fromDate;
    private final LocalDate toDate;
}