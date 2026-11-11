package org.example.telemetry.aggregate;

import java.time.Instant;

public record SessionMetadata(
        String studentId,
        String sessionId,
        Instant startTime,
        Instant endTime,
        long durationSeconds,
        long totalCompilationAttempts,
        long successfulCompilations,
        long failedCompilations,
        long totalEvents) {
}