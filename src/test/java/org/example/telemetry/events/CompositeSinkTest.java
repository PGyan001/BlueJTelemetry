package org.example.telemetry.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompositeSinkTest {

    @Test
    void forwardsEventsToAllTargets() {
        EventLog a = new EventLog();
        EventLog b = new EventLog();
        new CompositeSink(a, b).record(TelemetryEvent.of(EventType.EDIT, null));

        assertEquals(1, a.getEvents().size());
        assertEquals(1, b.getEvents().size());
    }
}