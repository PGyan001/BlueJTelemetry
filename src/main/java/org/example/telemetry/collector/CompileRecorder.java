package org.example.telemetry.collector;

import org.example.telemetry.events.EventSink;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CompileRecorder {
    private final EventSink sink;

    public CompileRecorder(EventSink sink) {
        this.sink = Objects.requireNonNull(sink);
    }

    public void started(List<String> files) {
        sink.record(TelemetryEvent.of(EventType.COMPILE_STARTED,
                Map.of("files", join(files))));
    }

    public void error(List<String> files, int line, String message) {
        String safeMessage = message == null ? "" : message;
        sink.record(TelemetryEvent.of(EventType.COMPILE_ERROR, Map.of(
                "files", join(files),
                "line", String.valueOf(line),
                "message", safeMessage,
                "category", ErrorClassifier.classify(safeMessage))));
    }

    public void succeeded(List<String> files) {
        sink.record(TelemetryEvent.of(EventType.COMPILE_SUCCESS,
                Map.of("files", join(files))));
    }

    private static String join(List<String> files) {
        return files == null ? "" : String.join(",", files);
    }
}
