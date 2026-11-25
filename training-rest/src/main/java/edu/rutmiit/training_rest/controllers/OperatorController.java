package edu.rutmiit.training_rest.controllers;

import edu.rutmiit.training_rest.repository.CardioMetricRepository;
import edu.rutmiit.training_rest.repository.UserRepository;
import edu.rutmiit.training_rest.repository.WorkoutRepository;
import edu.rutmiit.training_rest.repository.WorkoutSummaryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/operator")
public class OperatorController {
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final CardioMetricRepository cardioMetricRepository;
    private final WorkoutSummaryRepository workoutSummaryRepository;

    public OperatorController(UserRepository userRepository, WorkoutRepository workoutRepository,
                              CardioMetricRepository cardioMetricRepository, WorkoutSummaryRepository workoutSummaryRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.cardioMetricRepository = cardioMetricRepository;
        this.workoutSummaryRepository = workoutSummaryRepository;
    }

    @GetMapping("/diagnostics")
    @PreAuthorize("hasRole('OPERATOR')")
    public Map<String, Long> diagnostics() {
        return Map.of(
                "users", userRepository.count(),
                "workouts", workoutRepository.count(),
                "metrics", cardioMetricRepository.count(),
                "summaries", workoutSummaryRepository.count());
    }
}
