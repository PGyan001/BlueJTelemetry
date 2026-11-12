package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportDataFactoryTest {

    static final Instant T0 = Instant.parse("2026-11-09T10:00:00Z");

    static TelemetryEvent ev(long sec, EventType type, Map<String, String> details) {
        return new TelemetryEvent(T0.plusSeconds(sec), type, details);
    }

    static List<TelemetryEvent> sampleEvents() {
        return List.of(
                ev(0, EventType.PROJECT_CREATED, Map.of("project", "P")),
                ev(5, EventType.CLASS_CREATED, Map.of("class", "Human")),
                ev(10, EventType.COMPILE_STARTED, null),
                ev(11, EventType.COMPILE_ERROR, Map.of("category", "SYNTAX_ERROR")),
                ev(30, EventType.COMPILE_STARTED, null),
                ev(31, EventType.COMPILE_SUCCESS, null),
                ev(31, EventType.METHOD_DEFINED, Map.of("class", "Human", "signature", "public int getAge()")),
                ev(40, EventType.PROGRAM_RUN, Map.of("kind", "CONSTRUCTOR")),
                ev(45, EventType.PROGRAM_RUN, Map.of("kind", "INSTANCE_METHOD")),
                ev(45, EventType.RUNTIME_EXCEPTION, null),
                ev(50, EventType.CLASS_RENAMED, Map.of("class", "Person", "oldName", "Human")),
                ev(60, EventType.CLASS_DELETED, Map.of("class", "Person")));
    }

    static SessionMetadata meta(List<TelemetryEvent> events) {
        return new SessionMetadataBuilder().studentId("s1")
                .period(T0, T0.plusSeconds(120)).events(events).build();
    }

    @Test
    void buildsAllReportSections() {
        List<TelemetryEvent> events = sampleEvents();
        ReportData data = new ReportDataFactory(Duration.ofSeconds(60)).create(meta(events), events);

        assertEquals(2, data.compile().attempts());
        assertEquals(1, data.compile().successes());
        assertEquals(1L, data.errorFrequency().get("SYNTAX_ERROR"));
        assertEquals(2, data.programRuns());
        assertEquals(1, data.runtimeExceptions());
        assertEquals(Duration.ofSeconds(60), data.idleThreshold());
        assertTrue(data.activity().total().compareTo(Duration.ZERO) > 0);
    }

    @Test
    void timelineHasClassAndMethodEntriesInOrder() {
        List<TelemetryEvent> events = sampleEvents();
        List<DevelopmentTimeline.Entry> timeline = new DevelopmentTimeline().build(events);

        assertEquals(4, timeline.size());
        assertEquals("Class created", timeline.get(0).kind());
        assertEquals("Method defined", timeline.get(1).kind());
        assertEquals("public int getAge()", timeline.get(1).detail());
        assertEquals("was Human", timeline.get(2).detail());
        assertEquals("Class deleted", timeline.get(3).kind());
    }

    @Test
    void nullEventsAreTreatedAsEmpty() {
        ReportData data = new ReportDataFactory(Duration.ofSeconds(60)).create(meta(List.of()), null);
        assertEquals(0, data.programRuns());
        assertTrue(data.timeline().isEmpty());
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> new ReportDataFactory(null));
        assertThrows(NullPointerException.class,
                () -> new ReportDataFactory(Duration.ofSeconds(1)).create(null, List.of()));
    }
}
