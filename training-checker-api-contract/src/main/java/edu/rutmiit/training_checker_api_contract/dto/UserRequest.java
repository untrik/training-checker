package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Запрос на создание или обновление пользователя")
public record UserRequest(

    @Schema(description = "Имя пользователя", example = "untrick", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 100, message = "Имя пользователя не может превышать 100 символов")
    String username,

    @Schema(description = "Email пользователя", example = "zkirpa2005@example.ru",requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email пользователя не может быть пустым")
    @Size(max = 255, message = "Email не может превышать 255 символов")
    String email,

    @Schema(description = "Дата рождения пользователя", example = "2005-09-08")
    @Past(message = "Дата рождения должна быть в прошлом")
    LocalDate birthDate,

    @Schema(description = "Вес пользователя в кг", example = "70")
    @Min(value = 1, message = "Вес должен быть положительным")
    @Max(value = 1000, message = "Укажите корректный вес")
    Integer weightKg


    ){}
