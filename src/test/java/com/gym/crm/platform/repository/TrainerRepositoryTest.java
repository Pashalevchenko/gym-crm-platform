package com.gym.crm.platform.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.TrainingType;
import com.gym.crm.platform.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainer DAO DBUnit integration tests")
class TrainerRepositoryTest extends AbstractRepositoryTest<TrainerRepository> {

    private static final Long TRAINER_ID = 10L;
    private static final Long SECOND_TRAINER_ID = 12L;
    private static final Long TRAINER_USER_ID = 10L;
    private static final Long SECOND_TRAINER_USER_ID = 12L;
    private static final Long SPECIALIZATION_ID = 10L;
    private static final Long SECOND_SPECIALIZATION_ID = 12L;

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainer")
        void create_success() {
            Trainer trainer = buildTrainer("New", "Trainer", "new.trainer", SPECIALIZATION_ID);

            Trainer actual = repository.save(trainer);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();
            assertThat(actual.getUser().getFirstName()).isEqualTo("New");
            assertThat(actual.getUser().getLastName()).isEqualTo("Trainer");
            assertThat(actual.getUser().getUsername()).isEqualTo("new.trainer");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
            assertThat(actual.getSpecialization()).isNotNull();
            assertThat(actual.getSpecialization().getId()).isEqualTo(SPECIALIZATION_ID);

            Optional<Trainer> found = repository.findByUserUsername("new.trainer");

            assertThat(found).isPresent();
            Trainer saved = found.get();
            assertThat(saved.getId()).isEqualTo(actual.getId());
            assertThat(saved.getUser()).isNotNull();
            assertThat(saved.getUser().getId()).isEqualTo(actual.getUser().getId());
            assertThat(saved.getUser().getFirstName()).isEqualTo("New");
            assertThat(saved.getUser().getLastName()).isEqualTo("Trainer");
            assertThat(saved.getUser().getUsername()).isEqualTo("new.trainer");
            assertThat(saved.getUser().getPassword()).isEqualTo("12345");
            assertThat(saved.getUser().isActive()).isTrue();
            assertThat(saved.getSpecialization()).isNotNull();
            assertThat(saved.getSpecialization().getId()).isEqualTo(SPECIALIZATION_ID);
        }

        @Test
        @DisplayName("Should throw exception when trainer is null")
        void create_nullTrainer() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainer when trainer exists")
        void update_success() {
            Trainer existing = repository.findById(TRAINER_ID).orElseThrow();
            User user = User.builder()
                    .id(existing.getUser().getId())
                    .firstName("Updated")
                    .lastName("Trainer")
                    .username(existing.getUser().getUsername())
                    .password(existing.getUser().getPassword())
                    .isActive(existing.getUser().isActive())
                    .build();
            TrainingType specialization = TrainingType.builder()
                    .id(SECOND_SPECIALIZATION_ID)
                    .trainingTypeName("Strength Shenanigans")
                    .build();
            Trainer trainerToUpdate = Trainer.builder()
                    .id(existing.getId())
                    .user(user)
                    .specialization(specialization)
                    .build();

            Trainer actual = repository.save(trainerToUpdate);

            Optional<Trainer> found = repository.findById(actual.getId());
            assertThat(found).isPresent();
            Trainer updated = found.get();
            assertThat(updated.getId()).isEqualTo(TRAINER_ID);
            assertThat(updated.getUser().getId()).isEqualTo(TRAINER_USER_ID);
            assertThat(updated.getUser().getFirstName()).isEqualTo("Updated");
            assertThat(updated.getUser().getLastName()).isEqualTo("Trainer");
            assertThat(updated.getUser().getUsername()).isEqualTo("pavlo.plank");
            assertThat(updated.getUser().getPassword()).isEqualTo("12345");
            assertThat(updated.getUser().isActive()).isTrue();
            assertThat(updated.getSpecialization().getId()).isEqualTo(SECOND_SPECIALIZATION_ID);
            assertThat(updated.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");
        }

        @Test
        @DisplayName("Should throw exception when trainer is null")
        void update_nullTrainer() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainer when trainer with requested id exists")
        void findById_found() {
            Optional<Trainer> actual = repository.findById(TRAINER_ID);

            assertThat(actual).isPresent();
            Trainer found = actual.get();
            assertThat(found.getId()).isEqualTo(TRAINER_ID);
            assertThat(found.getUser().getId()).isEqualTo(TRAINER_USER_ID);
            assertThat(found.getUser().getFirstName()).isEqualTo("Pavlo");
            assertThat(found.getUser().getLastName()).isEqualTo("Plank");
            assertThat(found.getUser().getUsername()).isEqualTo("pavlo.plank");
            assertThat(found.getUser().getPassword()).isEqualTo("12345");
            assertThat(found.getUser().isActive()).isTrue();
            assertThat(found.getSpecialization().getId()).isEqualTo(SPECIALIZATION_ID);
            assertThat(found.getSpecialization().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should return empty optional when trainer with requested id does not exist")
        void findById_notFound() {
            Optional<Trainer> actual = repository.findById(999L);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainer when trainer with requested username exists")
        void findByUsername_found() {
            Optional<Trainer> actual = repository.findByUserUsername("fedir.foamroller");

            assertThat(actual).isPresent();
            Trainer found = actual.get();
            assertThat(found.getId()).isEqualTo(SECOND_TRAINER_ID);
            assertThat(found.getUser().getId()).isEqualTo(SECOND_TRAINER_USER_ID);
            assertThat(found.getUser().getFirstName()).isEqualTo("Fedir");
            assertThat(found.getUser().getLastName()).isEqualTo("Foamroller");
            assertThat(found.getUser().getUsername()).isEqualTo("fedir.foamroller");
            assertThat(found.getUser().getPassword()).isEqualTo("12345");
            assertThat(found.getUser().isActive()).isTrue();
            assertThat(found.getSpecialization().getId()).isEqualTo(SECOND_SPECIALIZATION_ID);
            assertThat(found.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");
        }

        @Test
        @DisplayName("Should return empty optional when trainer with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainer> actual = repository.findByUserUsername("ghost.trainer");

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainers")
        void findAll_success() {
            List<Trainer> actual = repository.findAll();

            assertThat(actual).hasSize(3);

            Trainer pavlo = actual.get(0);
            assertThat(pavlo.getId()).isEqualTo(10L);
            assertThat(pavlo.getUser()).isNotNull();
            assertThat(pavlo.getUser().getId()).isEqualTo(10L);
            assertThat(pavlo.getUser().getFirstName()).isEqualTo("Pavlo");
            assertThat(pavlo.getUser().getLastName()).isEqualTo("Plank");
            assertThat(pavlo.getUser().getUsername()).isEqualTo("pavlo.plank");
            assertThat(pavlo.getUser().getPassword()).isEqualTo("12345");
            assertThat(pavlo.getUser().isActive()).isTrue();
            assertThat(pavlo.getSpecialization()).isNotNull();
            assertThat(pavlo.getSpecialization().getId()).isEqualTo(10L);
            assertThat(pavlo.getSpecialization().getTrainingTypeName()).isEqualTo("Penguin Yoga");

            Trainer fedir = actual.get(1);
            assertThat(fedir.getUser()).isNotNull();
            assertThat(fedir.getUser().getId()).isEqualTo(12L);
            assertThat(fedir.getUser().getFirstName()).isEqualTo("Fedir");
            assertThat(fedir.getUser().getLastName()).isEqualTo("Foamroller");
            assertThat(fedir.getUser().getUsername()).isEqualTo("fedir.foamroller");
            assertThat(fedir.getUser().getPassword()).isEqualTo("12345");
            assertThat(fedir.getUser().isActive()).isTrue();
            assertThat(fedir.getSpecialization()).isNotNull();
            assertThat(fedir.getSpecialization().getId()).isEqualTo(12L);
            assertThat(fedir.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");

            Trainer ira = actual.get(2);
            assertThat(ira.getId()).isEqualTo(15L);
            assertThat(ira.getUser()).isNotNull();
            assertThat(ira.getUser().getId()).isEqualTo(15L);
            assertThat(ira.getUser().getFirstName()).isEqualTo("Ira");
            assertThat(ira.getUser().getLastName()).isEqualTo("Iron");
            assertThat(ira.getUser().getUsername()).isEqualTo("ira.iron");
            assertThat(ira.getUser().getPassword()).isEqualTo("12345");
            assertThat(ira.getUser().isActive()).isTrue();
            assertThat(ira.getSpecialization()).isNotNull();
            assertThat(ira.getSpecialization().getId()).isEqualTo(12L);
            assertThat(ira.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");
        }
    }

    private Trainer buildTrainer(String firstName, String lastName, String username, Long specializationId) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("12345")
                .isActive(true)
                .build();
        TrainingType specialization = TrainingType.builder()
                .id(specializationId)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }
}