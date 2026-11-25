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
import org.apache.coyote.Response;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Workouts", description = "Управление тренировками")
@RequestMapping(
        value = "/api/workouts",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface WorkoutApi {
    @Operation(
            summary = "Получить тренировку по ID",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Тренировка найдена")
    @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    EntityModel<WorkoutResponse> getWorkoutById(@Parameter(description = "ID тренировки",
            required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id);


    @Operation(
            summary = "Начать тренировку",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Тренировка создана. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь с указанным userId не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<WorkoutResponse>> createWorkout(@Parameter(description = "ID пользователя",
            required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id, @Valid @RequestBody WorkoutRequest request);

    @Operation(
            summary = "Создать метрику",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER))
    @ApiResponse(responseCode = "201", description = "Метрика создана.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Тренировка с указанным workoutId не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{workoutId}/metric")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<CardioMetricResponse>  createMetric(@Parameter(description = "ID тренировки",
            required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID workoutId, @Valid @RequestBody CardioMetricRequest request);

    @Operation(
            summary = "Закончить тренировку",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER))
    @ApiResponse(responseCode = "200", description = "Тренировка завершена.")
//    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
//            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Тренировка с указаным workoutId не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/finish")
    EntityModel<WorkoutResponse> finishWorkout(
            @Parameter(description = "ID тренировки",
                    required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id
//            @Valid @RequestBody FinishWorkoutRequest request
    );

    @Operation(
            summary = "Получить метрики по ID тренировки",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Тренировка найдена")
    @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/metrics")
    List<CardioMetricResponse> getMetricsByWorkoutId(@Parameter(description = "ID тренировки",
            required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id);

    @Operation(
            summary = "Получить конечную статистику по ID тренировки",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Тренировка найдена")
    @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/summary")
    WorkoutSummaryResponse getSummaryByWorkoutId(@Parameter(description = "ID тренировки",
            required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id);

//    @Operation(
//            summary = "Создать метрики тренировки",
//            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER))
//    @ApiResponse(responseCode = "201", description = "Метрики созданы. Location header содержит URI нового ресурса.")
//    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
//            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @ApiResponse(responseCode = "404", description = "Тренировка с указаным workoutId не найдена",
//            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PostMapping("/metric")
//    ResponseEntity<EntityModel<CardioMetricResponse>> createMetrics(
//            @Valid @RequestBody CardioMetricRequest request
//    );

    @Operation(
            summary = "Удалить тренировку со всеми метриками",
            security = @SecurityRequirement(name = TrainingCheckerApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Тренировка удалена")
    @ApiResponse(responseCode = "404", description = "Тренировку не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    EntityModel<WorkoutResponse> deleteWorkout(
            @Parameter(description = "ID тренировки", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id
    );
}
