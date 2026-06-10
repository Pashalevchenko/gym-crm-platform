package com.gym.crm.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Long id;

    @Column(name = "training_name", nullable = false, length = 50)
    private String trainingName;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Integer trainingDuration;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainee_id", nullable = false)
    @ToString.Exclude
    private Trainee trainee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    @ToString.Exclude
    private Trainer trainer;
}