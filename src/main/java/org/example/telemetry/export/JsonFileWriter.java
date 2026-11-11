package org.example.telemetry.export.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileWriter {

    public Path write(Path file, String json) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        return Files.writeString(file, json, StandardCharsets.UTF_8);
    }
}