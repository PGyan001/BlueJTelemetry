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
    private final Set<String> settling = new HashSet<>();
    private final Set<String> preloaded = new HashSet<>();

    public LifecycleRecorder(EventSink sink) {this.sink = Objects.requireNonNull(sink);}

    public synchronized void projectOpened(String project, boolean newlyCreated) {
        for (String name : preloaded) {
            knownClasses.add(key(project, name));
        }
        preloaded.clear();
        sink.record(TelemetryEvent.of(newlyCreated ? EventType.PROJECT_CREATED : EventType.PROJECT_OPENED,Map.of("project", nz(project))));
    }

    public synchronized void projectClosing(String project) {
        preloaded.clear();
        sink.record(TelemetryEvent.of(EventType.PROJECT_CLOSED, Map.of("project", nz(project))));
    }

    public synchronized void classStateChanged(String project, String className, boolean compiled) {
        String name = nz(className);
        if (nz(project).isEmpty()) {
            preloaded.add(name);
            return;
        }
        String key = key(project, name);
        Map<String, String> details = Map.of("project", project, "class", name, "compiled", String.valueOf(compiled));

        boolean isNew = knownClasses.add(key);
        if (isNew) {
            sink.record(TelemetryEvent.of(EventType.CLASS_CREATED, details));
            settling.add(key);
            return;
        }
        if (!compiled && settling.remove(key)) {return;}
        settling.remove(key);
        sink.record(TelemetryEvent.of(EventType.CLASS_MODIFIED, details));
        if (!compiled) {
            sink.record(TelemetryEvent.of(EventType.EDIT, details));
        }
    }

    public synchronized void classRenamed(String project, String oldName, String newName) {
        knownClasses.remove(key(project, oldName));
        settling.remove(key(project, oldName));
        String newKey = key(project, newName);
        knownClasses.add(newKey);
        settling.add(newKey);
        sink.record(TelemetryEvent.of(EventType.CLASS_RENAMED, Map.of(
            "project", nz(project), "oldName", nz(oldName), "class", nz(newName))));
    }

    public synchronized void classRemoved(String project, String className) {
        String k = key(project, className);
        knownClasses.remove(k);
        settling.remove(k);
        sink.record(TelemetryEvent.of(EventType.CLASS_DELETED, Map.of(
            "project", nz(project), "class", nz(className))));
    }

    private static String key(String project, String className) {return nz(project) + "::" + nz(className);}

    private static String nz(String s) {return s == null ? "" : s;}
}