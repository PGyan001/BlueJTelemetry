package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class ReportDataFactory {

    private final Duration idleThreshold;

    public ReportDataFactory(Duration idleThreshold) {
        this.idleThreshold = Objects.requireNonNull(idleThreshold);
    }

    public ReportData create(SessionMetadata session, List<TelemetryEvent> events) {
        Objects.requireNonNull(session);
        List<TelemetryEvent> list = events == null ? List.of() : events;
        return new ReportData(
                session,
                new ActivityTimeCalculator(idleThreshold).calculate(list),
                CompileStats.from(list),
                new ErrorFrequencyAggregator().aggregate(list),
                new DevelopmentTimeline().build(list),
                count(list, EventType.PROGRAM_RUN),
                count(list, EventType.RUNTIME_EXCEPTION),
                idleThreshold);
    }
