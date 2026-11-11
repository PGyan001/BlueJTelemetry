package org.example.telemetry.events;

import java.util.List;

public class CompositeSink implements EventSink{
    private final List<EventSink> targets;

    public CompositeSink(EventSink... targets) { this.targets = List.of(targets);}

    @Override
    public void record(TelemetryEvent event) {
        for (EventSink target : targets) {
            target.record(event);
        }
    }
}
