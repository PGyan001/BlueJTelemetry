package org.example.telemetry.export;

import org.example.telemetry.aggregate.ReportData;
import org.example.telemetry.aggregate.ReportDataFactory;
import org.example.telemetry.aggregate.SessionMetadata;
import org.example.telemetry.aggregate.SessionMetadataBuilder;
import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.TelemetryEvent;
import org.example.telemetry.export.json.JsonFileWriter;
import org.example.telemetry.export.json.TelemetryJsonSerializer;
import org.example.telemetry.export.pdf.PdfReportRenderer;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionExporter {

    public static final Duration DEFAULT_IDLE_THRESHOLD = Duration.ofSeconds(60);

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    private record Snapshot(Instant end, List<TelemetryEvent> events, SessionMetadata meta) {}

    private final EventLog log;
    private final Path outputDir;
