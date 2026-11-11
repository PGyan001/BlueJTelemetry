package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import java.util.List;

public record CompileStats(long attempts, long successes, long failures) {
    public static CompileStats from(List<TelemetryEvent> events) {
        long attempts = events.stream().filter(e -> e.type() == EventType.COMPILE_STARTED).count();
        long successes = events.stream().filter(e -> e.type() == EventType.COMPILE_SUCCESS).count();
        return new CompileStats(attempts, successes, Math.max(0, attempts - successes));
    }

    public double successRate() { return attempts == 0 ? 0.0 : (double) successes / attempts;}
}


