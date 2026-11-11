package org.example.telemetry.export;

import org.example.telemetry.aggregate.SessionMetadata;
import org.example.telemetry.aggregate.SessionMetadataBuilder;
import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.TelemetryEvent;
import org.example.telemetry.export.json.JsonFileWriter;
import org.example.telemetry.export.json.TelemetryJsonSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionExporter {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    private final EventLog log;
    private final Path outputDir;
    private final String studentId;
    private final String sessionId;
    private final Instant sessionStart;
    private final TelemetryJsonSerializer serializer = new TelemetryJsonSerializer();
    private final JsonFileWriter writer = new JsonFileWriter();

    public SessionExporter(EventLog log, Path outputDir, String studentId,
                           String sessionId, Instant sessionStart) {
        this.log = log;
        this.outputDir = outputDir;
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.sessionStart = sessionStart;
    }

    public Path exportJson() throws IOException {
        Instant end = Instant.now();
        List<TelemetryEvent> events = log.getEvents();
        SessionMetadata meta = new SessionMetadataBuilder()
                .studentId(studentId)
                .sessionId(sessionId)
                .period(sessionStart, end)
                .events(events)
                .build();
        Path file = outputDir.resolve("telemetry-" + STAMP.format(end) + ".json");
        return writer.write(file, serializer.serialize(meta, events));
    }
}