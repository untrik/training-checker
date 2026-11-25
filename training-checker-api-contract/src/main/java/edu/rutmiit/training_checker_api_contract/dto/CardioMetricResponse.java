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
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "metrics", itemRelation = "metric")
@Schema(description = "Информация о метрике")
public class CardioMetricResponse extends RepresentationModel<CardioMetricResponse> {

    @Schema(description = "Уникальный идентификатор метрики", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID id;

    @Schema(description = "Уникальный идентификатор тренировки")
    private final UUID workoutId;

    @Schema(description = "Время измерения", example = "2007-12-03T10:15:30")
    private final OffsetDateTime timestamp;

    @Schema(description = "Пульс", example = "144")
    private final Integer heartRate;

    @Schema(description = "Пройденная дистанция в километрах", example = "0.7")
    private final Double distanceKm;

    @Schema(description = "Количество сожжённых калорий", example = "220")
    private final Integer calories;

    @Schema(description = "Темп как время, за которое пройден километр (в минутах)", example = "4")
    private final Integer pace;
}
