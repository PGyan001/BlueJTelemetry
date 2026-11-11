package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AggregatorsTest {
    private final Instant t0 = Instant.parse("2026-11-09T10:00:00Z");

    private TelemetryEvent ev(long sec, EventType type, Map<String, String> details) {
        return new TelemetryEvent(t0.plusSeconds(sec), type, details);
    }

    @Test
    void shortGapsAreActiveAndLongGapsAreIdle() {
        List<TelemetryEvent> events = List.of(
                ev(0, EventType.EDIT, null),
                ev(30, EventType.EDIT, null),     // 30s gap: active
                ev(200, EventType.EDIT, null));   // 170s gap: idle
        ActivityTime t = new ActivityTimeCalculator(Duration.ofSeconds(60)).calculate(events);
        assertEquals(Duration.ofSeconds(30), t.active());
        assertEquals(Duration.ofSeconds(170), t.idle());
        assertEquals(Duration.ofSeconds(200), t.total());
    }

    @Test
    void editRightAfterCompileErrorIsIgnored() {
        List<TelemetryEvent> events = List.of(
                ev(0, EventType.COMPILE_ERROR, null),
                ev(0, EventType.EDIT, null),
                ev(10, EventType.EDIT, null));
        ActivityTime t = new ActivityTimeCalculator(Duration.ofSeconds(60)).calculate(events);
        assertEquals(Duration.ofSeconds(10), t.active());
    }

    @Test
    void timeAfterProjectCloseIsIdle() {
        List<TelemetryEvent> events = List.of(
                ev(0, EventType.PROJECT_CLOSED, null),
                ev(20, EventType.PROJECT_OPENED, null));
        ActivityTime t = new ActivityTimeCalculator(Duration.ofSeconds(60)).calculate(events);
        assertEquals(Duration.ZERO, t.active());
        assertEquals(Duration.ofSeconds(20), t.idle());
    }

    @Test
    void emptyAndSingleEventListsGiveZero() {
        ActivityTimeCalculator calc = new ActivityTimeCalculator(Duration.ofSeconds(60));
        assertEquals(Duration.ZERO, calc.calculate(List.of()).total());
        assertEquals(Duration.ZERO, calc.calculate(List.of(ev(0, EventType.EDIT, null))).total());
    }

    @Test
    void thresholdMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new ActivityTimeCalculator(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new ActivityTimeCalculator(null));
    }

    @Test
    void errorFrequencySortedByCount() {
        List<TelemetryEvent> events = List.of(
                ev(0, EventType.COMPILE_ERROR, Map.of("category", "SYNTAX_ERROR")),
                ev(1, EventType.COMPILE_ERROR, Map.of("category", "CANNOT_FIND_SYMBOL")),
                ev(2, EventType.COMPILE_ERROR, Map.of("category", "CANNOT_FIND_SYMBOL")),
                ev(3, EventType.COMPILE_ERROR, null),
                ev(4, EventType.EDIT, null));
        Map<String, Long> freq = new ErrorFrequencyAggregator().aggregate(events);
        assertEquals("CANNOT_FIND_SYMBOL", freq.keySet().iterator().next());
        assertEquals(2L, freq.get("CANNOT_FIND_SYMBOL"));
        assertEquals(1L, freq.get("OTHER"));
        assertEquals(3, freq.size());
    }

    @Test
    void compileStatsAndSuccessRate() {
        List<TelemetryEvent> events = List.of(
                ev(0, EventType.COMPILE_STARTED, null), ev(1, EventType.COMPILE_ERROR, null),
                ev(2, EventType.COMPILE_STARTED, null), ev(3, EventType.COMPILE_SUCCESS, null),
                ev(4, EventType.COMPILE_STARTED, null), ev(5, EventType.COMPILE_SUCCESS, null),
                ev(6, EventType.COMPILE_STARTED, null), ev(7, EventType.COMPILE_SUCCESS, null));
        CompileStats s = CompileStats.from(events);
        assertEquals(4, s.attempts());
        assertEquals(3, s.successes());
        assertEquals(1, s.failures());
        assertEquals(0.75, s.successRate(), 1e-9);
    }

    @Test
    void successRateIsZeroWithNoAttempts() {
        assertEquals(0.0, CompileStats.from(List.of()).successRate());
    }

    @Test
    void classTimelineKeepsOnlyClassEventsInOrder() {
        List<TelemetryEvent> events = List.of(
                ev(5, EventType.CLASS_DELETED, Map.of("class", "Dog")),
                ev(1, EventType.CLASS_CREATED, Map.of("class", "Dog")),
                ev(2, EventType.EDIT, Map.of("class", "Dog")));
        List<ClassTimeline.Entry> timeline = new ClassTimeline().build(events);
        assertEquals(2, timeline.size());
        assertEquals("CLASS_CREATED", timeline.get(0).action());
        assertEquals("Dog", timeline.get(1).className());
    }
}
