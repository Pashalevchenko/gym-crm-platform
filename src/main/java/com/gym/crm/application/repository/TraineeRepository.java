package com.gym.crm.application.repository;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @Modifying
    void deleteByUserUsername(String username);

    @Query("""
            from Trainer tr
            where tr.user.isActive = true
            and tr not in (
                select assignedTrainer
                from Trainee t
                join t.trainers assignedTrainer
                where t.user.username = :traineeUsername
            )
            """)
    List<Trainer> findNotAssignedTrainers(String traineeUsername);

    long countByUserIsActiveTrue();
}
