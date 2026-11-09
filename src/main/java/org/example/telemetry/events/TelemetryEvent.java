package org.example.telemetry.events;

import java.time.Instant;
import java.util.Map;

public record TelemetryEvent(Instant timestamp, EventType type, Map<String, String> details) {

    public TelemetryEvent {
        if (timestamp == null || type == null) {
            throw new IllegalArgumentException("timestamp and type required");
        }
        details = details == null ? Map.of() : Map.copyOf(details);
    }

    public static TelemetryEvent of(EventType type, Map<String, String> details) {
        return new TelemetryEvent(Instant.now(), type, details);
    }
}