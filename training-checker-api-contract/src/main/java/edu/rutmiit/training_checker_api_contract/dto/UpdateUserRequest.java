package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Schema(description = "Новое имя пользователя", example = "Вадим", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "имя пользователя не может быть пустым")
        @Size(max = 100, message = "Имя пользователя не может превышать 100 символов")
        String username,

        @Schema(description = "Новый email пользователя", example = "vkirpa2005@example.ru", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email(message = "Некорректный формат email")
        @NotBlank(message = "почта не может быть пустой")
        @Size(max = 255, message = "Email не может превышать 255 символов")
        String email,

        @Schema(description = "Новая дата рождения пользователя", example = "2005-09-08", requiredMode = Schema.RequiredMode.REQUIRED)
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        @Schema(description = "Новый вес пользователя в кг", example = "70", requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1, message = "Вес должен быть положительным")
        @Max(value = 1000, message = "Укажите корректный вес")
        Integer weightKg

) { }

