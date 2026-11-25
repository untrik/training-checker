package edu.rutmiit.grpcanalytics.controller;

import edu.rutmiit.grpcanalytics.service.AnalyticsDiagnosticMode;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("observability-demo")
@RequestMapping("/internal/diagnostics")
@PreAuthorize("hasRole('SERVICE')")
public class DiagnosticController {
    private final AnalyticsDiagnosticMode diagnostics;
    public DiagnosticController(AnalyticsDiagnosticMode diagnostics) { this.diagnostics = diagnostics; }

    @GetMapping("/mode")
    public AnalyticsDiagnosticMode.Setting current() { return diagnostics.current(); }

    @PostMapping("/mode")
    public AnalyticsDiagnosticMode.Setting change(@RequestBody Change request) {
        return diagnostics.change(request.mode(), request.seconds());
    }

    public record Change(AnalyticsDiagnosticMode.Mode mode, int seconds) {}
}
