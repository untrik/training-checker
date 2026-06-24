package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Запрос на завершение кардио тренировки")
public record FinishWorkoutRequest(
        @ArraySchema(
                arraySchema = @Schema(
                        description = "Список метрик, собранных во время тренировки",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = CardioMetricRequest.class)
        )
        @NotEmpty(message = "Список метрик не может быть пустым")
        List<@Valid CardioMetricRequest> metrics


//        @Schema(description = "Итоговая сводка по завершённой тренировке",
//                requiredMode = Schema.RequiredMode.REQUIRED,
//                implementation = WorkoutSummaryRequest.class
//        )
//        @NotNull(message = "Сводка тренировки не может быть пустой")
//        @Valid
//        WorkoutSummaryRequest summary
) {
}
