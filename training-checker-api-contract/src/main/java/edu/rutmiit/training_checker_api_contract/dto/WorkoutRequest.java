package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Запрос на создание тренировки")
public record WorkoutRequest(

//        @Schema(description = "ID пользователя", example = "20",
//             requiredMode = Schema.RequiredMode.REQUIRED)
//        @NotNull(message = "ID пользователя не может быть пустым")
//        Long userId,

        @Schema(description = "Тип кардио тренировки", example = "RUNNING", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Тип кардио тренировки не может быть пустым")
        WorkoutType type,

        @Schema(description = "Название тренировки", example = "Утренняя пробежка",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Название тренировки не может быть пустым")
        @Size(max = 250, message = "Название тренировки не может превышать 250 символов")
        String title

) {
}
