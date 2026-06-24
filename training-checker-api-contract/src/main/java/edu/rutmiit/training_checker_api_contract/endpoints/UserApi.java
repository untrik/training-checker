package edu.rutmiit.training_checker_api_contract.endpoints;

import edu.rutmiit.training_checker_api_contract.config.TrainingCheckerApiContractConfig;
import edu.rutmiit.training_checker_api_contract.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Управление пользователями")
@RequestMapping(
        value = "/api/users",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface UserApi {
    @Operation(summary = "Получить пользователя по ID",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER))
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    EntityModel<UserResponse> getUserById(@Parameter(description = "ID пользователя", required = true,
            example = "1") @PathVariable Long id);


    @Operation(
            summary = "Создать пользователя"
    )
    @ApiResponse(responseCode = "201", description = "Пользователь создан. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<UserResponse>> createUser(@Valid @RequestBody UserRequest request);


    @Operation(
            summary = "Частичное обновление пользователя (PATCH)",
            description = """
                    Обновляет только переданные поля (семантика JSON Merge Patch, RFC 7396).
                    Непереданные поля остаются без изменений.
                    """,
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<UserResponse> patchUser(
            @Parameter(description = "ID пользователя", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PatchUserRequest request
    );



    @Operation(
            summary = "Обновление пользователя (PUT)",
            description = """
                    Заменяет все поля пользователя. Для обновления отдельных полей используйте PATCH..
                    """,
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<UserResponse> updateUser(
            @Parameter(description = "ID пользователя", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    );

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя и все его тренировки (каскадное удаление).",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    EntityModel<UserResponse> deleteUser(
            @Parameter(description = "ID пользователя", required = true, example = "1") @PathVariable Long id
    );

    @Operation(
            summary = "Тренировки пользователя (суб-ресурс)",
            description = """
                    Возвращает постраничный список тренировок указанного пользователя.
                    Это суб-ресурс (концепция REST): /user/{id}/workouts.
                    Эквивалентен GET /workouts?userId={id}, но точнее отражает иерархию.
                    """,
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список тренировок пользователя")
    @ApiResponse(responseCode = "404", description = "пользователь не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/workouts")
    PagedModel<EntityModel<WorkoutResponse>> getWorkoutsByUser(
            @Parameter(description = "ID пользователя", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Фильтр по типу тренировки", example = "RUNNING") @RequestParam(required = false) WorkoutType type,
            @Parameter(description = "Фильтр по преодоленной дистанции в км", example = "4.2") @RequestParam(required = false) Double totalDistanceKm,
            @Parameter(description = "Фильтр по соженным калориям", example = "250") @RequestParam(required = false) Integer totalCalories,
            @Parameter(description = "Поиск по названию (substring, case-insensitive)", example = "Пробежка") @RequestParam(required = false) String titleSearch,
            @Parameter(description = "Номер страницы (0..N)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size
    );
}
