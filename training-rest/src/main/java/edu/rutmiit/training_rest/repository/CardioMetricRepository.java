package edu.rutmiit.training_rest.repository;

import edu.rutmiit.training_rest.domain.CardioMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardioMetricRepository extends JpaRepository<CardioMetricEntity, UUID> {
    List<CardioMetricEntity> findAllByWorkoutId(UUID workoutId);

    int countByWorkoutId(UUID workoutId);
}
