package org.example.telemetry.export;

import org.example.telemetry.aggregate.ReportData;

import java.io.IOException;
import java.nio.file.Path;

public interface ReportRenderer {
    Path render(ReportData data, Path file) throws IOException;
}
