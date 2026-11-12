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

    private static void assertIsPdf(Path file) throws Exception {
        byte[] bytes = Files.readAllBytes(file);
        assertTrue(bytes.length > 500);
        assertEquals("%PDF", new String(bytes, 0, 4, StandardCharsets.US_ASCII));
    }

    @Test
    void rendersFullReport(@TempDir Path dir) throws Exception {
        List<TelemetryEvent> events = List.of(
                new TelemetryEvent(T0, EventType.CLASS_CREATED, Map.of("class", "Human")),
                new TelemetryEvent(T0.plusSeconds(10), EventType.COMPILE_STARTED, null),
                new TelemetryEvent(T0.plusSeconds(11), EventType.COMPILE_ERROR,
                        Map.of("category", "SYNTAX_ERROR")),
                new TelemetryEvent(T0.plusSeconds(40), EventType.METHOD_DEFINED,
                        Map.of("class", "Human", "signature", "public int getAge()")),
                new TelemetryEvent(T0.plusSeconds(200), EventType.EDIT, null));

        Path file = new PdfReportRenderer().render(data(events), dir.resolve("out/report.pdf"));
        assertIsPdf(file);
    }

    @Test
    void rendersEmptySession(@TempDir Path dir) throws Exception {
        assertIsPdf(new PdfReportRenderer().render(data(List.of()), dir.resolve("empty.pdf")));
    }

    @Test
    void truncatesVeryLongTimelines(@TempDir Path dir) throws Exception {
        List<TelemetryEvent> events = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            events.add(new TelemetryEvent(T0.plusSeconds(i), EventType.METHOD_DEFINED,
                    Map.of("class", "A", "signature", "void m" + i + "()")));
        }
        assertIsPdf(new PdfReportRenderer().render(data(events), dir.resolve("long.pdf")));
    }

    @Test
    void formatsDurations() {
        assertEquals("1h 01m 05s", PdfReportRenderer.format(Duration.ofSeconds(3665)));
    }
}
