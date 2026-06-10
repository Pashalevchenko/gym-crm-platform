package com.gym.crm.application.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.repository.specification.TrainingSpecifications;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Training repository tests")
class TrainingRepositoryTest extends AbstractRepositoryTest<TrainingRepository> {

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("Should save training")
        void save_success() {
            Trainee trainee = entityManager.find(Trainee.class, 10L);
            Trainer trainer = entityManager.find(Trainer.class, 12L);
            TrainingType trainingType = entityManager.find(TrainingType.class, 10L);
            Training training = Training.builder()
                    .trainingName("New Penguin Yoga")
                    .trainingDate(LocalDate.of(2026, 5, 20))
                    .trainingDuration(60)
                    .trainee(trainee)
                    .trainer(trainer)
                    .trainingType(trainingType)
                    .build();

            Training actual = repository.save(training);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getTrainingName()).isEqualTo("New Penguin Yoga");
            assertThat(actual.getTrainingDate()).isEqualTo(LocalDate.of(2026, 5, 20));
            assertThat(actual.getTrainingDuration()).isEqualTo(60);
            assertThat(actual.getTrainee().getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(actual.getTrainer().getUser().getUsername()).isEqualTo("fedir.foamroller");
            assertThat(actual.getTrainingType().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should throw exception when training is null")
        void save_nullTraining() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findAll by trainee criteria")
    class FindAllByTraineeCriteriaTests {

        @Test
        @DisplayName("Should return trainings by trainee username")
        void findAll_byTraineeUsername() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .username("borys.burpee")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
        }

        @Test
        @DisplayName("Should return trainings by trainee username and date range")
        void findAll_byTraineeUsernameAndDateRange() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .username("borys.burpee")
                    .fromDate(LocalDate.of(2026, 4, 1))
                    .toDate(LocalDate.of(2026, 4, 30))
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
        }

        @Test
        @DisplayName("Should return trainings by trainer full name")
        void findAll_byTrainerName() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .trainerName("Pavlo Plank")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
            assertThat(actual.get(0).getTrainer().getUser().getUsername()).isEqualTo("pavlo.plank");
        }

        @Test
        @DisplayName("Should return trainings by partial trainer name ignoring case")
        void findAll_byPartialTrainerNameIgnoringCase() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .trainerName("pavlo")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainer().getUser().getUsername()).isEqualTo("pavlo.plank");
        }

        @Test
        @DisplayName("Should return trainings by training type name")
        void findAll_byTrainingTypeName() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .trainingTypeName("Penguin Yoga")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
            assertThat(actual.get(0).getTrainingType().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should return training by combined trainee criteria")
        void findAll_byCombinedTraineeCriteria() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .username("borys.burpee")
                    .fromDate(LocalDate.of(2026, 4, 1))
                    .toDate(LocalDate.of(2026, 4, 30))
                    .trainerName("Pavlo Plank")
                    .trainingTypeName("Penguin Yoga")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
        }

        @Test
        @DisplayName("Should return empty list when no training matches trainee criteria")
        void findAll_byTraineeCriteria_noMatches() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .username("missing.user")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("Should return all trainings when trainee filter is empty")
        void findAll_byEmptyTraineeCriteria() {
            TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTraineeCriteria(filter));

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(Training::getTrainingName)
                    .containsExactlyInAnyOrder("Morning Penguin Stretch", "Evening Strength");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findAll by trainer criteria")
    class FindAllByTrainerCriteriaTests {

        @Test
        @DisplayName("Should return trainings by trainer username")
        void findAll_byTrainerUsername() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .username("fedir.foamroller")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Evening Strength");
        }

        @Test
        @DisplayName("Should return trainings by trainer username and date range")
        void findAll_byTrainerUsernameAndDateRange() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .username("fedir.foamroller")
                    .fromDate(LocalDate.of(2026, 4, 1))
                    .toDate(LocalDate.of(2026, 4, 30))
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Evening Strength");
        }

        @Test
        @DisplayName("Should return trainings by trainee full name")
        void findAll_byTraineeName() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .traineeName("Borys Burpee")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
            assertThat(actual.get(0).getTrainee().getUser().getUsername()).isEqualTo("borys.burpee");
        }

        @Test
        @DisplayName("Should return trainings by partial trainee name ignoring case")
        void findAll_byPartialTraineeNameIgnoringCase() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .traineeName("borys")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainee().getUser().getUsername()).isEqualTo("borys.burpee");
        }

        @Test
        @DisplayName("Should return training by combined trainer criteria")
        void findAll_byCombinedTrainerCriteria() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .username("pavlo.plank")
                    .fromDate(LocalDate.of(2026, 4, 1))
                    .toDate(LocalDate.of(2026, 4, 30))
                    .traineeName("Borys Burpee")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(1);
            assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
        }

        @Test
        @DisplayName("Should return empty list when no training matches trainer criteria")
        void findAll_byTrainerCriteria_noMatches() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .username("missing.trainer")
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("Should return all trainings when trainer filter is empty")
        void findAll_byEmptyTrainerCriteria() {
            TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                    .build();

            List<Training> actual = repository.findAll(TrainingSpecifications.byTrainerCriteria(filter));

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(Training::getTrainingName)
                    .containsExactlyInAnyOrder("Morning Penguin Stretch", "Evening Strength");
        }
    }
}