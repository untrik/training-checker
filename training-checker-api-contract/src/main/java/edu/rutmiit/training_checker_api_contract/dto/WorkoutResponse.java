package edu.rutmiit.training_checker_api_contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false) // не включаем HATEOAS-ссылки в сравнение equals
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "workouts", itemRelation = "workout")
@Schema(description = "Информация о тренировке")
public class WorkoutResponse extends RepresentationModel<WorkoutResponse> {
    @Schema(description = "Уникальный идентификатор тренировки", example = "1")
    private final Long id;

    @Schema(description = "Информация о пользователе")
    private final UserResponse user;

    @Schema(description = "Тип кардио тренировки", example = "RUNNING")
    private final WorkoutType type;

    @Schema(description = "Название тренировки", example = "Утренняя пробежка")
    private final String title;

    @Schema(description = "Начало тренировки", example = "2007-12-03T10:15:30")
    private final OffsetDateTime startedAt;

    @Schema(description = "Конец тренировки", example = "2007-12-03T10:17:00")
    private final OffsetDateTime finishedAt;

    @Schema(description = "Метрики тренировки")
    private final List<CardioMetricResponse> metrics;

    @Schema(description = "Конечная статистика тренировки")
    private final WorkoutSummaryResponse workoutSummary;
}
