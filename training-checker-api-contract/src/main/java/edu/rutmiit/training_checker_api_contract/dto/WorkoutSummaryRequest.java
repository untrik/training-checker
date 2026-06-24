package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Запрос на создание статистики тренировки")
public record WorkoutSummaryRequest(
//        @Schema(description = "Уникальный идентификатор тренировки", example = "20",
//                requiredMode = Schema.RequiredMode.REQUIRED)
//        @NotNull(message = "ID тренировки не может быть пустым")
//        Long workoutId,

        @Schema(description = "Общая длительность тренировки в секундах", example = "3600",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Длительность не может быть пустой")
        @Positive(message = "Длительность должна быть больше 0")
        Long duration,

        @Schema(description = "Общая дистанция в километрах", example = "5.4",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Дистанция не может быть пустой")
        @Positive(message = "Дистанция должна быть больше 0")
        Double totalDistanceKm,

        @Schema(description = "Средний пульс", example = "148",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Средний пульс не может быть пустым")
        @Positive(message = "пульс должен быть больше 0")
        Integer avgHeartRate,

        @Schema(description = "Максимальный пульс", example = "182",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Максимальный пульс не может быть пустым")
        @Positive(message = "Максимальный пульс должен быть больше 0")
        Integer maxHeartRate,

        @Schema(description = "Общее количество сожжённых калорий", example = "430",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Калории не могут быть пустыми")
        @Positive(message = "Калории должны быть больше 0")
        Integer totalCalories,

        @Schema(description = "Средний темп", example = "5",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Темп не может быть пустым")
        @Positive(message = "Темп должен быть больше 0")
        Integer avgPace
        ) {
}
