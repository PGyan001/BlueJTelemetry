package org.example.telemetry.extension;

import org.example.telemetry.events.EventSink;
import org.example.telemetry.events.TelemetryEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class FileDebugSink implements EventSink {
    private final Path file;

    FileDebugSink(Path file) { this.file = file; }

    @Override
    public void record(TelemetryEvent event) {
        try { Files.writeString(file, event + System.lineSeparator(), StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND); } 
        catch (IOException ignored) {}
    }
}