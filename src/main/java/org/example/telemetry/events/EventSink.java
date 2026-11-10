package org.example.telemetry.events;

public interface EventSink {
    void record(TelemetryEvent event);
}
