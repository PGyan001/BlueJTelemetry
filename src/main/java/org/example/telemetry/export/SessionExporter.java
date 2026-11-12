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
    private final String studentId;
    private final String sessionId;
    private final Instant sessionStart;
    private final TelemetryJsonSerializer serializer = new TelemetryJsonSerializer();
    private final JsonFileWriter writer = new JsonFileWriter();
    private final ReportRenderer renderer = new PdfReportRenderer();
    private final ReportDataFactory reportFactory = new ReportDataFactory(DEFAULT_IDLE_THRESHOLD);

    public SessionExporter(EventLog log, Path outputDir, String studentId,
                           String sessionId, Instant sessionStart) {
        this.log = log;
        this.outputDir = outputDir;
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.sessionStart = sessionStart;
    }

    public Path exportJson() throws IOException {
        return writeJson(snapshot());
    }

    public Path exportPdf() throws IOException {
        return writePdf(snapshot());
    }

    /** Writes the JSON file and the PDF report from the same snapshot of events. */
    public List<Path> exportAll() throws IOException {
        Snapshot snapshot = snapshot();
        return List.of(writeJson(snapshot), writePdf(snapshot));
    }

    private Snapshot snapshot() {
        Instant end = Instant.now();
        List<TelemetryEvent> events = log.getEvents();
        SessionMetadata meta = new SessionMetadataBuilder()
                .studentId(studentId)
                .sessionId(sessionId)
                .period(sessionStart, end)
                .events(events)
                .build();
        return new Snapshot(end, events, meta);
    }

    private Path writeJson(Snapshot s) throws IOException {
        Path file = outputDir.resolve("telemetry-" + STAMP.format(s.end()) + ".json");
        return writer.write(file, serializer.serialize(s.meta(), s.events()));
    }

    private Path writePdf(Snapshot s) throws IOException {
        ReportData data = reportFactory.create(s.meta(), s.events());
        Path file = outputDir.resolve("telemetry-report-" + STAMP.format(s.end()) + ".pdf");
        return renderer.render(data, file);
    }
}
