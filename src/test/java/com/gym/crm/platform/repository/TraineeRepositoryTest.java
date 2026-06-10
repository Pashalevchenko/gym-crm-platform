package com.gym.crm.platform.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.platform.entity.Trainee;
import com.gym.crm.platform.entity.Trainer;
import com.gym.crm.platform.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainee DAO DBUnit integration tests")
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository> {

    private static final Long TRAINEE_ID = 1L;
    private static final Long SECOND_TRAINEE_ID = 2L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainee")
        void create_success() {
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 100");
            jdbcTemplate.execute("ALTER TABLE trainees ALTER COLUMN id RESTART WITH 100");

            Trainee trainee = buildTrainee("New", "Trainee", "new.trainee");

            Trainee actual = repository.save(trainee);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(actual.getAddress()).isEqualTo("Kyiv");
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();
            assertThat(actual.getUser().getFirstName()).isEqualTo("New");
            assertThat(actual.getUser().getLastName()).isEqualTo("Trainee");
            assertThat(actual.getUser().getUsername()).isEqualTo("new.trainee");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();

            Optional<Trainee> found = repository.findByUserUsername("new.trainee");

            assertThat(found).isPresent();
            Trainee saved = found.get();
            assertThat(saved.getId()).isEqualTo(actual.getId());
            assertThat(saved.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(saved.getAddress()).isEqualTo("Kyiv");
            assertThat(saved.getUser()).isNotNull();
            assertThat(saved.getUser().getId()).isEqualTo(actual.getUser().getId());
            assertThat(saved.getUser().getFirstName()).isEqualTo("New");
            assertThat(saved.getUser().getLastName()).isEqualTo("Trainee");
            assertThat(saved.getUser().getUsername()).isEqualTo("new.trainee");
            assertThat(saved.getUser().getPassword()).isEqualTo("12345");
            assertThat(saved.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void create_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainee when trainee exists")
        void update_success() {
            Trainee existing = repository.findById(TRAINEE_ID).orElseThrow();
            User user = User.builder()
                    .id(existing.getUser().getId())
                    .firstName("Updated")
                    .lastName("Burpee")
                    .username(existing.getUser().getUsername())
                    .password(existing.getUser().getPassword())
                    .isActive(existing.getUser().isActive())
                    .build();
            Trainee traineeToUpdate = Trainee.builder()
                    .id(existing.getId())
                    .dateOfBirth(LocalDate.of(1999, 9, 9))
                    .address("Lviv")
                    .user(user)
                    .build();

            Trainee actual = repository.save(traineeToUpdate);

            Optional<Trainee> found = repository.findById(actual.getId());
            assertThat(found).isPresent();
            Trainee updatedTrainee = found.get();
            assertThat(updatedTrainee.getId()).isEqualTo(TRAINEE_ID);
            assertThat(updatedTrainee.getDateOfBirth()).isEqualTo(LocalDate.of(1999, 9, 9));
            assertThat(updatedTrainee.getAddress()).isEqualTo("Lviv");
            assertThat(updatedTrainee.getUser().getId()).isEqualTo(TRAINEE_ID);
            assertThat(updatedTrainee.getUser().getFirstName()).isEqualTo("Updated");
            assertThat(updatedTrainee.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(updatedTrainee.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(updatedTrainee.getUser().getPassword()).isEqualTo("12345");
            assertThat(updatedTrainee.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void update_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested id exists")
        void findById_found() {
            Optional<Trainee> actual = repository.findById(TRAINEE_ID);

            assertThat(actual).isPresent();
            Trainee found = actual.get();
            assertThat(found.getId()).isEqualTo(TRAINEE_ID);
            assertThat(found.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(found.getAddress()).isEqualTo("Kyiv");
            assertThat(found.getUser().getId()).isEqualTo(TRAINEE_ID);
            assertThat(found.getUser().getFirstName()).isEqualTo("Borys");
            assertThat(found.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(found.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(found.getUser().getPassword()).isEqualTo("12345");
            assertThat(found.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested id does not exist")
        void findById_notFound() {
            Optional<Trainee> actual = repository.findById(999L);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested username exists")
        void findByUsername_found() {
            Optional<Trainee> actual = repository.findByUserUsername("marta.muscle");

            assertThat(actual).isPresent();
            Trainee found = actual.get();
            assertThat(found.getId()).isEqualTo(SECOND_TRAINEE_ID);
            assertThat(found.getDateOfBirth()).isEqualTo(LocalDate.of(2001, 2, 2));
            assertThat(found.getAddress()).isEqualTo("Lviv");
            assertThat(found.getUser().getId()).isEqualTo(SECOND_TRAINEE_ID);
            assertThat(found.getUser().getFirstName()).isEqualTo("Marta");
            assertThat(found.getUser().getLastName()).isEqualTo("Muscle");
            assertThat(found.getUser().getUsername()).isEqualTo("marta.muscle");
            assertThat(found.getUser().getPassword()).isEqualTo("12345");
            assertThat(found.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainee> actual = repository.findByUserUsername("ghost.gains");

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainees")
        void findAll_success() {
            List<Trainee> actual = repository.findAll();

            assertThat(actual).hasSize(2);
            Trainee borys = actual.get(0);
            assertThat(borys.getId()).isEqualTo(1L);
            assertThat(borys.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(borys.getAddress()).isEqualTo("Kyiv");
            assertThat(borys.getUser()).isNotNull();
            assertThat(borys.getUser().getId()).isEqualTo(1L);
            assertThat(borys.getUser().getFirstName()).isEqualTo("Borys");
            assertThat(borys.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(borys.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(borys.getUser().getPassword()).isEqualTo("12345");
            assertThat(borys.getUser().isActive()).isTrue();

            Trainee marta = actual.get(1);
            assertThat(marta.getId()).isEqualTo(2L);
            assertThat(marta.getDateOfBirth()).isEqualTo(LocalDate.of(2001, 2, 2));
            assertThat(marta.getAddress()).isEqualTo("Lviv");
            assertThat(marta.getUser()).isNotNull();
            assertThat(marta.getUser().getId()).isEqualTo(2L);
            assertThat(marta.getUser().getFirstName()).isEqualTo("Marta");
            assertThat(marta.getUser().getLastName()).isEqualTo("Muscle");
            assertThat(marta.getUser().getUsername()).isEqualTo("marta.muscle");
            assertThat(marta.getUser().getPassword()).isEqualTo("12345");
            assertThat(marta.getUser().isActive()).isTrue();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("deleteByUsername")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested username exists")
        void deleteByUsername_success() {
            repository.deleteByUserUsername("marta.muscle");

            assertThat(repository.findById(SECOND_TRAINEE_ID)).isEmpty();
            assertThat(repository.findByUserUsername("marta.muscle")).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = CLEAN_INSERT)
    @DisplayName("findNotAssignedTrainers")
    class FindNotAssignedTrainersTests {

        @Test
        @DisplayName("Should return trainers not assigned to trainee")
        void findNotAssignedTrainers_success() {
            List<Trainer> actual = repository.findNotAssignedTrainers("borys.burpee");

            assertThat(actual)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
        }
    }

    private Trainee buildTrainee(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("12345")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();
    }
}