package edu.rutmiit.training_checker_api_contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public enum WorkoutType {
    @Schema(description = "Бег") RUNNING,
    @Schema(description = "Ходьба") WALKING,
    @Schema(description = "Велотренажер")CYCLING
}
