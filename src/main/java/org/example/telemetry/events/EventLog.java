package org.example.telemetry.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLog {

    private final List<TelemetryEvent> events = new ArrayList<>();

    // Synchronized for multithreading
    public synchronized void record(TelemetryEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        events.add(event);
    }

    public synchronized List<TelemetryEvent> getEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public synchronized long countByType(EventType type) {
        return events.stream().filter(e -> e.type() == type).count();
    }
}