package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ErrorFrequencyAggregator {
    public Map<String, Long> aggregate(List<TelemetryEvent> events) {
        Map<String, Long> counts = new TreeMap<>();
        for (TelemetryEvent e : events) {
            if (e.type() == EventType.COMPILE_ERROR) {
                counts.merge(e.details().getOrDefault("category", "OTHER"), 1L, Long::sum);
            }
        }
        Map<String, Long> sorted = new LinkedHashMap<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(en -> sorted.put(en.getKey(), en.getValue()));
        return sorted;
    }
}
