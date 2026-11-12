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
