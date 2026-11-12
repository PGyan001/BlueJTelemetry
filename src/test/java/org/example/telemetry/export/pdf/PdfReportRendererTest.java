package org.example.telemetry.export.pdf;

import org.example.telemetry.aggregate.ReportData;
import org.example.telemetry.aggregate.ReportDataFactory;
import org.example.telemetry.aggregate.SessionMetadataBuilder;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportRendererTest {

    private static final Instant T0 = Instant.parse("2026-11-09T10:00:00Z");

    private static ReportData data(List<TelemetryEvent> events) {
        var meta = new SessionMetadataBuilder().studentId("s1")
                .period(T0, T0.plusSeconds(300)).events(events).build();
        return new ReportDataFactory(Duration.ofSeconds(60)).create(meta, events);
    }
