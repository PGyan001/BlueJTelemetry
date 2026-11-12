package org.example.telemetry.collector;

import org.example.telemetry.events.EventSink;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MethodRecorder {

    private final EventSink sink;
    private final MethodExtractor extractor = new MethodExtractor();
    private final Map<String, Set<String>> known = new HashMap<>();

    public MethodRecorder(EventSink sink) {
        this.sink = Objects.requireNonNull(sink);
    }

    /** Records a METHOD_DEFINED event for each signature not seen before in this class. */
    public synchronized void sourceCompiled(String className, String source) {
        String cls = className == null ? "" : className;
        Set<String> seen = known.computeIfAbsent(cls, k -> new HashSet<>());
        for (String signature : extractor.extract(cls, source)) {
            if (seen.add(signature)) {
                sink.record(TelemetryEvent.of(EventType.METHOD_DEFINED,
                        Map.of("class", cls, "signature", signature)));
            }
