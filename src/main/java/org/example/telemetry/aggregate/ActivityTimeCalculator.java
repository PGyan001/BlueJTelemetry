package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActivityTimeCalculator {
    private static final Duration ERROR_EDIT_WINDOW = Duration.ofSeconds(1);
    private final Duration idleThreshold;

    public ActivityTimeCalculator(Duration idleThreshold) {
        if (idleThreshold == null || idleThreshold.isNegative() || idleThreshold.isZero()) {
            throw new IllegalArgumentException("idleThreshold must be positive");
        }
        this.idleThreshold = idleThreshold;
    }

    public ActivityTime calculate(List<TelemetryEvent> events) {
        List<TelemetryEvent> relevant = relevantEvents(events);
        Duration active = Duration.ZERO;
        Duration idle = Duration.ZERO;

        for (int i = 1; i < relevant.size(); i++) {
            TelemetryEvent prev = relevant.get(i-1);
            Duration gap = Duration.between(prev.timestamp(), relevant.get(i).timestamp());
            if (prev.type() != EventType.PROJECT_CLOSED && gap.compareTo(idleThreshold) <=0) { active = active.plus(gap);} 
            else { idle = idle.plus(gap); }
        }
        return new ActivityTime(active, idle);
    }

    private List<TelemetryEvent> relevantEvents(List<TelemetryEvent> events) {
        List<TelemetryEvent> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparing(TelemetryEvent::timestamp));

        List<TelemetryEvent> result = new ArrayList<>();
        Instant lastError = null;
        for (TelemetryEvent e : sorted) {
            if (e.type() == EventType.COMPILE_ERROR) { lastError = e.timestamp(); }
            boolean errorNoise = e.type() == EventType.EDIT && lastError != null
                    && Duration.between(lastError, e.timestamp()).compareTo(ERROR_EDIT_WINDOW) <= 0;
            if (!errorNoise) {result.add(e);}
        }
        return result;
    }
}
