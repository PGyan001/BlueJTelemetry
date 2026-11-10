package org.example.telemetry.collector;

import org.example.telemetry.events.EventSink;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LifecycleRecorder {
    private final EventSink sink;
    private final Set<String> knownClasses = new HashSet<>();

    public LifecycleRecorder(EventSink sink) {
        this.sink = Objects.requireNonNull(sink);
    }

    public void projectOpened(String project) {
        sink.record(TelemetryEvent.of(EventType.PROJECT_OPENED, Map.of("project", nz(project))));
    }

    public void projectClosing(String project) {
        sink.record(TelemetryEvent.of(EventType.PROJECT_CLOSED, Map.of("project", nz(project))));
    }

    public synchronized void classStateChanged(String project, String className, boolean compiled) {
        String key = key(project, className);
        Map<String, String> details = Map.of(
                "project", nz(project),
                "class", nz(className),
                "compiled", String.valueOf(compiled));

        if (knownClasses.add(key)) {
            sink.record(TelemetryEvent.of(EventType.CLASS_CREATED, details));
        } else {
            sink.record(TelemetryEvent.of(EventType.CLASS_MODIFIED, details));
            if (!compiled) {
                sink.record(TelemetryEvent.of(EventType.EDIT, details));
            }
        }
    }

    public synchronized void classRenamed(String project, String oldName, String newName) {
        knownClasses.remove(key(project, oldName));
        knownClasses.add(key(project, newName));
        sink.record(TelemetryEvent.of(EventType.CLASS_RENAMED, Map.of(
                "project", nz(project),
                "oldName", nz(oldName),
                "class", nz(newName))));
    }

    public synchronized void classRemoved(String project, String className) {
        knownClasses.remove(key(project, className));
        sink.record(TelemetryEvent.of(EventType.CLASS_DELETED, Map.of(
                "project", nz(project),
                "class", nz(className))));
    }

    private static String key(String project, String className) {
        return nz(project) + "::" + nz(className);
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}