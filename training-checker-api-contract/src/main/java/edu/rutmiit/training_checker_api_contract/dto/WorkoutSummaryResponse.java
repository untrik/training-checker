package edu.rutmiit.training_checker_api_contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.UUID;


@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "workoutSummaries", itemRelation = "workoutSummary")
@Schema(description = "Конечная статистика тренировки")
public class WorkoutSummaryResponse extends RepresentationModel<WorkoutSummaryResponse> {
    @Schema(description = "Уникальный идентификатор тренировки", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID workoutId;

    @Schema(description = "Общая длительность тренировки в секундах", example = "3600")
    private final Long duration;

    @Schema(description = "Общая дистанция в километрах", example = "5.4")
    private final Double totalDistanceKm;

    @Schema(description = "Средний пульс", example = "148")
    private final Integer avgHeartRate;

    @Schema(description = "Максимальный пульс", example = "182")
    private final Integer maxHeartRate;

    @Schema(description = "Общее количество сожжённых калорий", example = "430")
    private final Integer totalCalories;

    @Schema(description = "Средний темп", example = "5")
    private final Double avgPace;

}
