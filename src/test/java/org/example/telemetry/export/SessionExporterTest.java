package org.example.telemetry.export;

import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class SessionExporterTest {
    @Test
    void writesParseableFile(@TempDir Path dir) throws Exception {
        EventLog log = new EventLog();
        log.record(TelemetryEvent.of(EventType.COMPILE_STARTED, null));

        Path file = new SessionExporter(log, dir.resolve("out"), "s1", "sess", Instant.now().minusSeconds(5)).exportJson();

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.contains("\"studentId\": \"s1\""));
        assertTrue(content.contains("COMPILE_STARTED"));
    }
}