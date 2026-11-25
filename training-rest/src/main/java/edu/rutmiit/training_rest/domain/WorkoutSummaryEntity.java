package edu.rutmiit.training_rest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "workout_summaries")
public class WorkoutSummaryEntity {

    @Id
    private UUID workoutId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutEntity workout;

    @Column(nullable = false)
    private Long duration;

    @Column(name = "total_distance_km", nullable = false)
    private Double totalDistanceKm;

    @Column(name = "avg_heart_rate", nullable = false)
    private Integer avgHeartRate;

    @Column(name = "max_heart_rate", nullable = false)
    private Integer maxHeartRate;

    @Column(name = "total_calories", nullable = false)
    private Integer totalCalories;

    @Column(name = "avg_pace", nullable = false)
    private Double avgPace;

    @Version
    private long version;

    protected WorkoutSummaryEntity() {
    }

    public WorkoutSummaryEntity(WorkoutEntity workout, Long duration,
                                Double totalDistanceKm, Integer avgHeartRate, Integer maxHeartRate,
                                Integer totalCalories, Double avgPace) {
        this.workout = workout;
        this.duration = duration;
        this.totalDistanceKm = totalDistanceKm;
        this.avgHeartRate = avgHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.totalCalories = totalCalories;
        this.avgPace = avgPace;
    }
}
