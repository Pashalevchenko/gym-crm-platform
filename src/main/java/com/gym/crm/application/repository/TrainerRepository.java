package com.gym.crm.application.repository;

import com.gym.crm.application.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    long countByUserIsActiveTrue();
}
