package org.example.telemetry.events;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//
class EventLogTest {

    @Test
    void recordsEventsInOrder() {
        EventLog log = new EventLog();
        log.record(TelemetryEvent.of(EventType.COMPILE_ERROR, Map.of("line", "12")));
        log.record(TelemetryEvent.of(EventType.COMPILE_SUCCESS, null));

        assertEquals(2, log.getEvents().size());
        assertEquals(EventType.COMPILE_ERROR, log.getEvents().get(0).type());
    }

    @Test
    void countsByType() {
        EventLog log = new EventLog();
        log.record(TelemetryEvent.of(EventType.COMPILE_ERROR, null));
        log.record(TelemetryEvent.of(EventType.COMPILE_ERROR, null));
        log.record(TelemetryEvent.of(EventType.COMPILE_SUCCESS, null));

        assertEquals(2, log.countByType(EventType.COMPILE_ERROR));
        assertEquals(1, log.countByType(EventType.COMPILE_SUCCESS));
        assertEquals(0, log.countByType(EventType.PROGRAM_RUN));
    }

    @Test
    void returnedListCannotBeModified() {
        EventLog log = new EventLog();
        assertThrows(UnsupportedOperationException.class,
                () -> log.getEvents().add(TelemetryEvent.of(EventType.EDIT, null)));
    }

    @Test
    void rejectsNullEvent() {
        assertThrows(IllegalArgumentException.class, () -> new EventLog().record(null));
    }

    @Test
    void eventRequiresTimestampAndType() {
        assertThrows(IllegalArgumentException.class,
                () -> new TelemetryEvent(null, EventType.EDIT, null));
        assertThrows(IllegalArgumentException.class,
                () -> new TelemetryEvent(Instant.now(), null, null));
    }

    @Test
    void nullDetailsBecomeEmptyMap() {
        assertTrue(TelemetryEvent.of(EventType.EDIT, null).details().isEmpty());
    }
}