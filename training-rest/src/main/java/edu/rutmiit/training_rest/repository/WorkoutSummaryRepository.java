package edu.rutmiit.training_rest.repository;

import edu.rutmiit.training_rest.domain.WorkoutSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutSummaryRepository extends JpaRepository<WorkoutSummaryEntity, UUID> {
}
