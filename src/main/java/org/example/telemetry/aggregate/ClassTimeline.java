package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassTimeline {
    public record Entry(Instant time, String className, String action) {}

    private static final Set<EventType> CLASS_EVENTS =
            Set.of(EventType.CLASS_CREATED, EventType.CLASS_RENAMED, EventType.CLASS_DELETED);

    public List<Entry> build(List<TelemetryEvent> events) {
        List<Entry> entries = new ArrayList<>();
        for (TelemetryEvent e : events) {
            if (CLASS_EVENTS.contains(e.type())) {
                entries.add(new Entry(e.timestamp(),
                        e.details().getOrDefault("class", ""), e.type().name()));
            }
        }
        entries.sort(java.util.Comparator.comparing(Entry::time));
        return entries;
    }
}
