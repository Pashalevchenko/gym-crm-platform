package com.gym.crm.application.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.gym.crm.application.config.DbUnitConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

@DataJpaTest
@ActiveProfiles("test")
@Import(DbUnitConfig.class)
@TestExecutionListeners(
        value = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T repository;

    @Autowired
    protected EntityManager entityManager;
}