package org.example.telemetry.export.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.telemetry.aggregate.SessionMetadata;
import org.example.telemetry.aggregate.SessionMetadataBuilder;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TelemetryJsonSerializerTest {
    private final Instant t0 = Instant.parse("2026-11-09T10:00:00Z");

    private List<TelemetryEvent> sample() {
        return List.of(
            new TelemetryEvent(t0, EventType.COMPILE_STARTED, Map.of("files", "A.java")),
            new TelemetryEvent(t0.plusSeconds(1), EventType.COMPILE_ERROR, Map.of("line", "5", "message", "';' expected \"quoted\"")),
            new TelemetryEvent(t0.plusSeconds(5), EventType.COMPILE_STARTED, null),
            new TelemetryEvent(t0.plusSeconds(6), EventType.COMPILE_SUCCESS, null));
    }

    private SessionMetadata meta(List<TelemetryEvent> events) {
        return new SessionMetadataBuilder()
            .studentId("s123")
            .period(t0, t0.plusSeconds(60))
            .events(events)
            .build();
    }

    @Test
    void outputIsValidJsonWithExpectedStructure() {
        List<TelemetryEvent> events = sample();
        String json = new org.example.telemetry.export.json.TelemetryJsonSerializer().serialize(meta(events), events);

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        assertEquals("1.0", root.get("schemaVersion").getAsString());
        JsonObject session = root.getAsJsonObject("session");
        assertEquals("s123", session.get("studentId").getAsString());
        assertEquals(60, session.get("durationSeconds").getAsLong());
        assertEquals(2, session.get("totalCompilationAttempts").getAsLong());
        assertEquals(1, session.get("successfulCompilations").getAsLong());
        assertEquals(1, session.get("failedCompilations").getAsLong());

        JsonArray arr = root.getAsJsonArray("events");
        assertEquals(4, arr.size());
        assertEquals("2026-11-09T10:00:00Z", arr.get(0).getAsJsonObject().get("timestamp").getAsString());
    }

    @Test
    void specialCharactersSurviveRoundTrip() {
        List<TelemetryEvent> events = sample();
        String json = new org.example.telemetry.export.json.TelemetryJsonSerializer().serialize(meta(events), events);
        JsonObject details = JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("events").get(1).getAsJsonObject().getAsJsonObject("details");
        assertEquals("';' expected \"quoted\"", details.get("message").getAsString());
    }

    @Test
    void emptyEventListIsValid() {
        String json = new org.example.telemetry.export.json.TelemetryJsonSerializer().serialize(meta(List.of()), List.of());
        assertEquals(0, JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("events").size());
    }

    @Test
    void builderRejectsInvalidPeriod() {
        assertThrows(IllegalArgumentException.class,
                () -> new SessionMetadataBuilder().period(t0, t0.minusSeconds(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new SessionMetadataBuilder().period(null, t0));
    }

    @Test
    void blankStudentIdKeepsPlaceholder() {
        SessionMetadata m = new SessionMetadataBuilder().studentId("  ").build();
        assertEquals("STUDENT_ID_PLACEHOLDER", m.studentId());
    }

    @Test
    void blankSessionIdKeepsGeneratedId() {
        SessionMetadata m = new SessionMetadataBuilder().sessionId(" ").build();
        assertFalse(m.sessionId().isBlank());
    }

    @Test
    void nullEventsTreatedAsEmpty() {
        SessionMetadata m = new SessionMetadataBuilder().events(null).build();
        assertEquals(0, m.totalEvents());
    }
}