package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class SessionMetadataBuilder {
    private String studentId = "STUDENT_ID_PLACEHOLDER";
    private String sessionId = UUID.randomUUID().toString();
    private Instant start = Instant.now();
    private Instant end = Instant.now();
    private List<TelemetryEvent> events = List.of();

    public SessionMetadataBuilder studentId(String id) {
        if (id != null && !id.isBlank()) {
            this.studentId = id;
        }
        return this;
    }

    public SessionMetadataBuilder sessionId(String id) {
        if (id != null && !id.isBlank()) {
            this.sessionId = id;
        }
        return this;
    }

    public SessionMetadataBuilder period(Instant start, Instant end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("invalid session period");
        }
        this.start = start;
        this.end = end;
        return this;
    }

    public SessionMetadataBuilder events(List<TelemetryEvent> events) {
        this.events = events == null ? List.of() : events;
        return this;
    }

    public SessionMetadata build() {
        long attempts = count(EventType.COMPILE_STARTED);
        long successes = count(EventType.COMPILE_SUCCESS);
        long failures = Math.max(0, attempts - successes);
        return new SessionMetadata(studentId, sessionId, start, end,
            Duration.between(start, end).getSeconds(),
            attempts, successes, failures, events.size());
    }

    private long count(EventType type) { return events.stream().filter(e -> e.type() == type).count();}
}