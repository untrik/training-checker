package edu.rutmiit.training_rest.controllers;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutRequest;
import edu.rutmiit.training_rest.exception.ApiException;
import edu.rutmiit.training_rest.service.IdempotentWorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RestController
public class IdempotentWorkoutController {

    private final IdempotentWorkoutService workouts;

    public IdempotentWorkoutController(IdempotentWorkoutService workouts) {
        this.workouts = workouts;
    }

    @PostMapping("/api/workouts/{userId}/idempotent")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR')")
    public ResponseEntity<JsonNode> create(Principal principal, @PathVariable("userId") UUID userId,
            @RequestHeader("Idempotency-Key") String rawKey, @Valid @RequestBody WorkoutRequest request) {

        if (!rawKey.matches("(?i)^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Idempotency-Key должен быть полным UUID без кавычек");
        }

        var result = workouts.create(principal.getName(), UUID.fromString(rawKey), request, userId);

        return ResponseEntity
                .created(URI.create("/api/workouts/" + result.workoutId()))
                .header("Idempotency-Replayed", Boolean.toString(result.replayed()))
                .body(result.responseBody());
    }
}