package com.gym.crm.platform.repository;

import com.gym.crm.platform.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    List<TrainingType> findAll();

    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}
