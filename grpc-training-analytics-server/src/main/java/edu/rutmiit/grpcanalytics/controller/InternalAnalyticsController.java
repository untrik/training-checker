package edu.rutmiit.grpcanalytics.controller;

import edu.rutmiit.grpcanalytics.service.AnalyticsAvailabilityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalAnalyticsController {
    private final AnalyticsAvailabilityService analytics;
    public InternalAnalyticsController(AnalyticsAvailabilityService analytics) { this.analytics = analytics; }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('SERVICE')")
    public Map<String, String> analytics() { return analytics.analytics(); }
}
