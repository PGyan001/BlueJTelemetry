package org.example.telemetry.collector;

import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvocationRecorderTest {

    @Test
    void normalRunRecordsOnlyProgramRun() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Main", "main", ExitStatus.NORMAL, "done");

        assertEquals(1, log.countByType(EventType.PROGRAM_RUN));
        assertEquals(0, log.countByType(EventType.RUNTIME_EXCEPTION));
        assertEquals("NORMAL", log.getEvents().get(0).details().get("status"));
    }

    @Test
    void exceptionRunAlsoRecordsRuntimeException() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Main", "main", ExitStatus.EXCEPTION, "NullPointerException");

        assertEquals(1, log.countByType(EventType.PROGRAM_RUN));
        assertEquals(1, log.countByType(EventType.RUNTIME_EXCEPTION));
    }

    @Test
    void longResultsAreTruncated() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("A", "b", ExitStatus.NORMAL, "x".repeat(500));
        assertEquals(200, log.getEvents().get(0).details().get("result").length());
    }

    @Test
    void nullsDefaultSafely() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished(null, null, null, null);
        assertEquals("UNKNOWN", log.getEvents().get(0).details().get("status"));
    }

    @Test
    void constructorIsClassifiedAndResultDropped() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Human", "", null, new String[]{"12"},
                ExitStatus.NORMAL, "BObject instanceName=null Class Name=Human");

        var d = log.getEvents().get(0).details();
        assertEquals("CONSTRUCTOR", d.get("kind"));
        assertEquals("12", d.get("params"));
        assertEquals("", d.get("result"));
    }

    @Test
    void exceptionOnObjectIsInstanceMethod() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Human", "", "human1", new String[0], ExitStatus.EXCEPTION, "");

        assertEquals("INSTANCE_METHOD", log.getEvents().get(0).details().get("kind"));
        assertEquals(1, log.countByType(EventType.RUNTIME_EXCEPTION));
    }

    @Test
    void successfulStaticCallIsStaticMethod() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Main", "main", "", new String[]{"{}"}, ExitStatus.NORMAL, "");
        assertEquals("STATIC_METHOD", log.getEvents().get(0).details().get("kind"));
    }

    @Test
    void methodReturningAnObjectIsNotMistakenForAConstructor() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Human", "getFriend", "human1", new String[0],
                ExitStatus.NORMAL, "BObject instanceName=null Class Name=Human");

        var d = log.getEvents().get(0).details();
        assertEquals("INSTANCE_METHOD", d.get("kind"));
        assertEquals("BObject instanceName=null Class Name=Human", d.get("result"));
    }

    @Test
    void unknownKindWhenBlueJGivesNoClues() {
        assertEquals("STATIC_OR_UNKNOWN", InvocationRecorder.kind("", "", ""));
        assertEquals("STATIC_OR_UNKNOWN", InvocationRecorder.kind("", "", null));
    }

    @Test
    void terminatedRunIsRecordedWithoutException() {
        EventLog log = new EventLog();
        new InvocationRecorder(log).finished("Main", "run", "", null, ExitStatus.TERMINATED, "");
        assertEquals("TERMINATED", log.getEvents().get(0).details().get("status"));
        assertEquals(0, log.countByType(EventType.RUNTIME_EXCEPTION));
    }
}
