package org.example.telemetry.events;

import java.util.Objects;

public class SafeSink implements EventSink {
    private final EventSink delegate;
    private volatile Throwable lastFailure;

    public SafeSink(EventSink delegate) { this.delegate = Objects.requireNonNull(delegate); }

    @Override
    public void record(TelemetryEvent event) {
        try {
            delegate.record(event);
        } catch (RuntimeException e) {
            lastFailure = e;
        }
    }

    public Throwable lastFailure() { return lastFailure; }
}
