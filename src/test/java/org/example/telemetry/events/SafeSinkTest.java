package org.example.telemetry.events;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SafeSinkTest {
    @Test
    void swallowsFailuresFromTheDelegate() {
        SafeSink safe = new SafeSink(e -> { throw new IllegalStateException("boom"); });

        assertDoesNotThrow(() -> safe.record(TelemetryEvent.of(EventType.EDIT, null)));
        assertTrue(safe.lastFailure() instanceof IllegalStateException);
    }

    @Test
    void passesEventsThroughWhenHealthy() {
        EventLog log = new EventLog();
        SafeSink safe = new SafeSink(log);
        safe.record(TelemetryEvent.of(EventType.EDIT, null));

        assertEquals(1, log.getEvents().size());
        assertNull(safe.lastFailure());
    }

    @Test
    void aBrokenSecondaryTargetDoesNotStopTheLog() {
        EventLog log = new EventLog();
        EventSink broken = e -> { throw new RuntimeException("disk full"); };
        EventSink sink = new SafeSink(new CompositeSink(log, new SafeSink(broken)));

        sink.record(TelemetryEvent.of(EventType.COMPILE_STARTED, null));
        assertEquals(1, log.countByType(EventType.COMPILE_STARTED));
    }

    @Test
    void rejectsNullDelegate() {
        assertThrows(NullPointerException.class, () -> new SafeSink(null));
    }
}
