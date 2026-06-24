package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Частичное обновление пользователя (PATCH). Передайте только те поля, которые нужно изменить.")
public record PatchUserRequest(
        @Schema(description = "Новое имя пользователя", example = "Вадим")
        @Size(max = 100, message = "Имя пользователя не может превышать 100 символов")
        String username,

        @Schema(description = "Новый email пользователя", example = "vkirpa2005@example.ru")
        @Email(message = "Некорректный формат email")
        @Size(max = 255, message = "Email не может превышать 255 символов")
        String email,

        @Schema(description = "Новая дата рождения пользователя", example = "2005-09-08")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        @Schema(description = "Новый вес пользователя в кг", example = "70")
        @Min(value = 1, message = "Вес должен быть положительным")
        @Max(value = 1000, message = "Укажите корректный вес")
        Integer weightKg

) { }
