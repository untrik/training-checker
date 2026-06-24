package edu.rutmiit.training_checker_api_contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "users", itemRelation = "user")
@Schema(description = "Информация о пользователе")
public class UserResponse extends RepresentationModel<UserResponse> {
    @Schema(description = "Уникальный идентификатор пользователя", example = "20")
    private final Long id;

    @Schema(description = "Имя пользователя", example = "untrick")
    private final  String username;

    @Schema(description = "Email пользователя", example = "zkirpa2005@example.ru")
    private final String email;

    @Schema(description = "Дата рождения пользователя", example = "2005-09-08")
    private final LocalDate birthDate;

    @Schema(description = "Вес пользователя в кг", example = "70")
    private final Integer weightKg;
}
