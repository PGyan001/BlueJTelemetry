package org.example.telemetry.export.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.telemetry.aggregate.SessionMetadata;
import org.example.telemetry.events.TelemetryEvent;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TelemetryJsonSerializer {
    public static final String SCHEMA_VERSION = "1.0";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String serialize(SessionMetadata meta, List<TelemetryEvent> events) {
        JsonObject root = new JsonObject();
        root.addProperty("schemaVersion", SCHEMA_VERSION);
        root.add("session", sessionJson(meta));

        JsonArray array = new JsonArray();
        for (TelemetryEvent event : events) {
            array.add(eventJson(event));
        }
        root.add("events", array);
        return gson.toJson(root);
    }

    private JsonObject sessionJson(SessionMetadata m) {
        JsonObject o = new JsonObject();
        o.addProperty("studentId", m.studentId());
        o.addProperty("sessionId", m.sessionId());
        o.addProperty("startTime", m.startTime().toString());
        o.addProperty("endTime", m.endTime().toString());
        o.addProperty("durationSeconds", m.durationSeconds());
        o.addProperty("totalCompilationAttempts", m.totalCompilationAttempts());
        o.addProperty("successfulCompilations", m.successfulCompilations());
        o.addProperty("failedCompilations", m.failedCompilations());
        o.addProperty("totalEvents", m.totalEvents());
        return o;
    }

    private JsonObject eventJson(TelemetryEvent e) {
        JsonObject o = new JsonObject();
        o.addProperty("timestamp", e.timestamp().toString());
        o.addProperty("type", e.type().name());
        JsonObject details = new JsonObject();
        for (Map.Entry<String, String> d : new TreeMap<>(e.details()).entrySet()) {
            details.addProperty(d.getKey(), d.getValue());
        }
        o.add("details", details);
        return o;
    }
}