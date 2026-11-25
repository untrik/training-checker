package edu.rutmiit.grpcanalytics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AnalyticsAvailabilityService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsAvailabilityService.class);
    private final ObjectProvider<AnalyticsDiagnosticMode> diagnostics;

    public AnalyticsAvailabilityService(ObjectProvider<AnalyticsDiagnosticMode> diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Map<String, String> analytics() {
        try {
            AnalyticsDiagnosticMode mode = diagnostics.getIfAvailable();
            if (mode != null) mode.beforeCall();
            log.atInfo().addKeyValue("event", "analytics.completed")
                    .addKeyValue("outcome", "success").log("Analytics availability checked");
            return Map.of("status", "ok", "message", "Analytics service is available");
        } catch (RuntimeException failure) {
            log.atWarn().addKeyValue("event", "analytics.failed")
                    .addKeyValue("outcome", "failure")
                    .addKeyValue("failureType", failure.getClass().getSimpleName())
                    .log("Analytics availability failed");
            throw failure;
        }
    }
}
