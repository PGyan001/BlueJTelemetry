package org.example.telemetry.collector;

import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompileRecorderTest {

    @Test
    void recordsStartedErrorAndSuccess() {
        EventLog log = new EventLog();
        CompileRecorder recorder = new CompileRecorder(log);

        recorder.started(List.of("A.java", "B.java"));
        recorder.error(List.of("A.java"), 7, "cannot find symbol");
        recorder.succeeded(List.of("A.java"));

        assertEquals(1, log.countByType(EventType.COMPILE_STARTED));
        assertEquals(1, log.countByType(EventType.COMPILE_ERROR));
        assertEquals(1, log.countByType(EventType.COMPILE_SUCCESS));
        assertEquals("A.java,B.java", log.getEvents().get(0).details().get("files"));
    }

    @Test
    void errorEventCarriesLineMessageAndCategory() {
        EventLog log = new EventLog();
        new CompileRecorder(log).error(List.of("A.java"), 12, "';' expected");

        TelemetryEvent e = log.getEvents().get(0);
        assertEquals("12", e.details().get("line"));
        assertEquals("';' expected", e.details().get("message"));
        assertEquals("SYNTAX_ERROR", e.details().get("category"));
    }

    @Test
    void toleratesNullInputs() {
        EventLog log = new EventLog();
        CompileRecorder recorder = new CompileRecorder(log);
        recorder.started(null);
        recorder.error(null, 0, null);
        assertEquals(2, log.getEvents().size());
    }

    @Test
    void rejectsNullSink() {
        assertThrows(NullPointerException.class, () -> new CompileRecorder(null));
    }
}