package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_rest.client.AnalyticsClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Profile("!session-auth")
public class WorkoutAnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(WorkoutAnalyticsService.class);
    private final WorkoutService workouts;
    private final AnalyticsClient analytics;
    private final MeterRegistry registry;
    private final AtomicInteger active = new AtomicInteger();
    private final Map<String, Counter> requests;
    private final Map<String, Timer> durations;

    public WorkoutAnalyticsService(WorkoutService workouts, AnalyticsClient analytics, MeterRegistry registry) {
        this.workouts = workouts;
        this.analytics = analytics;
        this.registry = registry;
        requests = Map.of("success", registry.counter("training.analytics.requests", "result", "success"),
                "failure", registry.counter("training.analytics.requests", "result", "failure"));
        durations = Map.of("success", timer("success"), "failure", timer("failure"));
        Gauge.builder("training.analytics.active", active, AtomicInteger::get)
                .description("Currently executing workout analytics operations").register(registry);
    }

    private Timer timer(String result) {
        return Timer.builder("training.analytics.processing")
                .description("Backend operation including workout lookup and downstream HTTP call")
                .tag("result", result)
                .serviceLevelObjectives(Duration.ofMillis(500), Duration.ofSeconds(1))
                .publishPercentileHistogram().register(registry);
    }

    public Map<String, String> analytics(UUID workoutId) {
        active.incrementAndGet();
        Timer.Sample sample = Timer.start(registry);
        String result = "failure";
        try {
            workouts.findById(workoutId);
            Map<String, String> response = analytics.getAnalytics();
            result = "success";
            log.atInfo().addKeyValue("event", "training.analytics.completed")
                    .addKeyValue("outcome", result).log("Workout analytics completed");
            return response;
        } catch (RuntimeException failure) {
            log.atWarn().addKeyValue("event", "training.analytics.failed")
                    .addKeyValue("outcome", result)
                    .addKeyValue("failureType", failure.getClass().getSimpleName())
                    .log("Workout analytics failed");
            throw failure;
        } finally {
            try {
                sample.stop(durations.get(result));
                requests.get(result).increment();
            } finally {
                active.decrementAndGet();
            }
        }
    }
}
