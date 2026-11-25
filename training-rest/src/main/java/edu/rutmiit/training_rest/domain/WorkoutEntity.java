package edu.rutmiit.training_rest.domain;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "workouts")
public class WorkoutEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false)
    private OffsetDateTime startedAt;

    @Column
    private OffsetDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkoutType type;

    @Version
    private long version;

    protected WorkoutEntity() {
    }

    public WorkoutEntity(UUID id, UserEntity user, String title,
                         OffsetDateTime startedAt, OffsetDateTime finishedAt, WorkoutType type) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.type = type;
    }
}
