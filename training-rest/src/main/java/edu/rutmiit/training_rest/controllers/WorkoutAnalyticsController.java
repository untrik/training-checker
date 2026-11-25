package edu.rutmiit.training_rest.controllers;

import edu.rutmiit.training_rest.service.WorkoutAnalyticsService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
@Profile("!session-auth")
public class WorkoutAnalyticsController {
    private final WorkoutAnalyticsService analytics;

    public WorkoutAnalyticsController(WorkoutAnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/api/workouts/{id}/analytics")
    public Map<String, String> analytics(@PathVariable UUID id) {
        return analytics.analytics(id);
    }
}
