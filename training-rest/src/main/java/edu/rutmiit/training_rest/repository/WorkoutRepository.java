package edu.rutmiit.training_rest.repository;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutType;
import edu.rutmiit.training_rest.domain.WorkoutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<WorkoutEntity, UUID> {
    List<WorkoutEntity> findAllByUserId(UUID userId);
   
    @Query("""
            select w
            from WorkoutEntity w
            where w.user.id = :userId and (:type is null or w.type = :type)
              and coalesce(lower(w.title), '') like coalesce(:titleSearch, '%')
              and (:totalDistanceKm is null or exists (
                        select 1
                        from WorkoutSummaryEntity s
                        where s.workout = w and s.totalDistanceKm >= :totalDistanceKm))
              and (:totalCalories is null or exists (
                        select 1
                        from WorkoutSummaryEntity s
                        where s.workout = w
                          and s.totalCalories >= :totalCalories))
            """)
    Page<WorkoutEntity> findByFilters(
            @Param("userId") UUID userId,
            @Param("type") WorkoutType type,
            @Param("totalDistanceKm") Double totalDistanceKm,
            @Param("totalCalories") Integer totalCalories,
            @Param("titleSearch") String titleSearch,
            Pageable pageable
    );
}
