package org.example.telemetry.collector;

import org.example.telemetry.events.EventSink;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.util.Map;
import java.util.Objects;

public class InvocationRecorder {

    private static final int MAX_SUMMARY = 200;

    private final EventSink sink;

    public InvocationRecorder(EventSink sink) {
        this.sink = Objects.requireNonNull(sink);
    }

    public void finished(String className, String methodName, ExitStatus status, String result) {
        String cls = className == null ? "" : className;
        String method = methodName == null ? "" : methodName;
        String summary = truncate(result);
        ExitStatus st = status == null ? ExitStatus.UNKNOWN : status;

        sink.record(TelemetryEvent.of(EventType.PROGRAM_RUN, Map.of(
                "class", cls,
                "method", method,
                "status", st.name(),
                "result", summary)));

        if (st == ExitStatus.EXCEPTION) {
            sink.record(TelemetryEvent.of(EventType.RUNTIME_EXCEPTION, Map.of(
                    "class", cls,
                    "method", method,
                    "message", summary)));
        }
    }

    private static String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_SUMMARY ? text : text.substring(0, MAX_SUMMARY);
    }
}