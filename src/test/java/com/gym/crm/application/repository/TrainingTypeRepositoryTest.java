package com.gym.crm.application.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Training type DAO DBUnit integration tests")
class TrainingTypeRepositoryTest extends AbstractRepositoryTest<TrainingTypeRepository> {

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all training types")
        void findAll_success() {
            List<TrainingType> actual = repository.findAll();

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(TrainingType::getTrainingTypeName)
                    .containsExactlyInAnyOrder("Penguin Yoga", "Strength Shenanigans");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findByName")
    class FindByNameTests {

        @Test
        @DisplayName("Should return training type when name exists")
        void findByName_whenNameExists_shouldReturnTrainingType() {
            Optional<TrainingType> actual = repository.findByTrainingTypeName("Penguin Yoga");

            assertThat(actual).isPresent();
            assertThat(actual.get().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should return empty optional when name does not exist")
        void findByName_whenNameDoesNotExist_shouldReturnEmptyOptional() {
            Optional<TrainingType> actual = repository.findByTrainingTypeName("Boxing");

            assertThat(actual).isEmpty();
        }
    }
}