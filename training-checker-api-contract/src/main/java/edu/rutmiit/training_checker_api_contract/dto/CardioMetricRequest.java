package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Schema(description = "Запрос на отправку метрики для кардио тренировки")
public record CardioMetricRequest(
        @Schema(description = "Время измерения", example = "2007-12-03T10:15:30", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Время измерения не может быть пустым")
        OffsetDateTime timestamp,

        @Schema(description = "Пульс", example = "144",requiredMode = Schema.RequiredMode.REQUIRED)
//      TODO: спросить нужно ли ограничение на пульс
//        @Min(value = 1,message = "")
//        @Max(250)
        @NotNull
        Integer heartRate,

        @Schema(description = "Пройденная дистанция в километрах",example = "0.7",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Double distanceKm,

        @Schema(description = "Количество сожжённых калорий", example = "220",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Integer calories,

        @Schema(description = "Темп как время, за которое пройден километр (в минутах)", example = "4",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Integer pace

        ) { }
