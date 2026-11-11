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
        finished(className, methodName, null, null, status, result);
    }

    public void finished(String className, String methodName, String objectName,
                         String[] params, ExitStatus status, String result) {
        String cls = nz(className);
        String method = nz(methodName);
        String object = nz(objectName);
        String kind = kind(object, result);
        ExitStatus st = status == null ? ExitStatus.UNKNOWN : status;
        // A constructor's result is just the new object's description, so it isn't worth storing.
        String summary = "CONSTRUCTOR".equals(kind) ? "" : truncate(result);

        sink.record(TelemetryEvent.of(EventType.PROGRAM_RUN, Map.of(
                "class", cls,
                "method", method,
                "object", object,
                "params", params == null ? "" : String.join(", ", params),
                "kind", kind,
                "status", st.name(),
                "result", summary)));

        if (st == ExitStatus.EXCEPTION) {
            sink.record(TelemetryEvent.of(EventType.RUNTIME_EXCEPTION, Map.of(
                    "class", cls,
                    "method", method,
                    "object", object,
                    "message", summary)));
        }
    }

    /** Heuristic from observed BlueJ 6.0.1 behaviour: constructors have a BObject result and no object name. */
    private static String kind(String object, String result) {
        if (result != null && result.startsWith("BObject")) {
            return "CONSTRUCTOR";
        }
        return object.isEmpty() ? "STATIC_OR_UNKNOWN" : "INSTANCE_METHOD";
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_SUMMARY ? text : text.substring(0, MAX_SUMMARY);
    }
}