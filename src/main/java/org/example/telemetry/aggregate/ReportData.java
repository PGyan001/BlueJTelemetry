package org.example.telemetry.aggregate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public record ReportData(
        SessionMetadata session,
        ActivityTime activity,
        CompileStats compile,
        Map<String, Long> errorFrequency,
        List<DevelopmentTimeline.Entry> timeline,
        long programRuns,
        long runtimeExceptions,
        Duration idleThreshold) {
}
