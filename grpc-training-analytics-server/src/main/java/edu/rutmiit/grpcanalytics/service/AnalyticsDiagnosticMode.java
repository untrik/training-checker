package edu.rutmiit.grpcanalytics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Profile("observability-demo")
public class AnalyticsDiagnosticMode {
    public enum Mode { NORMAL, SLOW, FAIL }
    public record Setting(Mode mode, Instant expiresAt) {}
    private static final Logger log = LoggerFactory.getLogger(AnalyticsDiagnosticMode.class);
    private final Clock clock;
    private final AtomicReference<Setting> setting =
            new AtomicReference<>(new Setting(Mode.NORMAL, Instant.EPOCH));

    public AnalyticsDiagnosticMode() { this(Clock.systemUTC()); }
    AnalyticsDiagnosticMode(Clock clock) { this.clock = clock; }

    public Setting current() {
        while (true) {
            Setting value = setting.get();
            if (value.mode() == Mode.NORMAL || clock.instant().isBefore(value.expiresAt())) return value;
            Setting normal = new Setting(Mode.NORMAL, Instant.EPOCH);
            if (setting.compareAndSet(value, normal)) return normal;
        }
    }

    public Setting change(Mode mode, int seconds) {
        if (mode == null || seconds < 1 || seconds > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mode required, seconds must be 1..120");
        }
        Setting next = new Setting(mode, mode == Mode.NORMAL ? Instant.EPOCH : clock.instant().plusSeconds(seconds));
        setting.set(next);
        log.atInfo().addKeyValue("event", "analytics.diagnostic.changed").addKeyValue("mode", mode.name()).log("Diagnostic mode changed");
        return next;
    }

    public void beforeCall() {
        switch (current().mode()) {
            case NORMAL -> { }
            case FAIL -> throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Local diagnostic failure");
            case SLOW -> {
                try { Thread.sleep(750); }
                catch (InterruptedException failure) {
                    Thread.currentThread().interrupt();
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Request interrupted");
                }
            }
        }
    }
}
