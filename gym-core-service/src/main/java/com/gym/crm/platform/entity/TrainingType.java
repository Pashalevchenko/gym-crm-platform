package com.gym.crm.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "training_types")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "training_type_name", nullable = false, unique = true, length = 100)
    private String trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    @Builder.Default
    @ToString.Exclude
    private Set<Training> trainings = new HashSet<>();

    @OneToMany(mappedBy = "specialization")
    @Builder.Default
    @ToString.Exclude
    private Set<Trainer> trainers = new HashSet<>();
}