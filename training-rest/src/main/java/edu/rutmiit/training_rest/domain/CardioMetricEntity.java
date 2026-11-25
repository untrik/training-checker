package edu.rutmiit.training_rest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cardio_metrics")
public class CardioMetricEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutEntity workout;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private Integer pace;

    @Version
    private long version;

    protected CardioMetricEntity() {
    }

    public CardioMetricEntity(UUID id, WorkoutEntity workout, OffsetDateTime timestamp,
                              Integer heartRate, Double distanceKm, Integer calories, Integer pace) {
        this.id = id;
        this.workout = workout;
        this.timestamp = timestamp;
        this.heartRate = heartRate;
        this.distanceKm = distanceKm;
        this.calories = calories;
        this.pace = pace;
    }
}
