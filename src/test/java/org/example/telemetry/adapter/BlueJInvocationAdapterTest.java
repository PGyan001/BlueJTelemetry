package org.example.telemetry.adapter;

import bluej.extensions2.event.InvocationFinishedEvent.EventType;
import org.example.telemetry.collector.ExitStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Only the pure mapping is tested; building real BlueJ events needs a running BlueJ. */
class BlueJInvocationAdapterTest {
    @Test
    void mapsEveryBlueJExitTypeAndNull() {
        assertEquals(ExitStatus.NORMAL, BlueJInvocationAdapter.toStatus(EventType.NORMAL_EXIT));
        assertEquals(ExitStatus.EXCEPTION, BlueJInvocationAdapter.toStatus(EventType.EXCEPTION_EXIT));
        assertEquals(ExitStatus.TERMINATED, BlueJInvocationAdapter.toStatus(EventType.TERMINATED_EXIT));
        assertEquals(ExitStatus.UNKNOWN, BlueJInvocationAdapter.toStatus(EventType.UNKNOWN_EXIT));
        assertEquals(ExitStatus.UNKNOWN, BlueJInvocationAdapter.toStatus(null));
    }
}
