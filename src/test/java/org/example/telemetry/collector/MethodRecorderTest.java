package org.example.telemetry.collector;

import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodRecorderTest {

    @Test
    void recordsEachSignatureOnlyOnce() {
        EventLog log = new EventLog();
        MethodRecorder r = new MethodRecorder(log);

        r.sourceCompiled("A", "class A { void one() { } }");
        r.sourceCompiled("A", "class A { void one() { } }");
        assertEquals(1, log.countByType(EventType.METHOD_DEFINED));

        r.sourceCompiled("A", "class A { void one() { } void two() { } }");
        assertEquals(2, log.countByType(EventType.METHOD_DEFINED));
        assertEquals("void two()", log.getEvents().get(1).details().get("signature"));
    }

    @Test
    void sameMethodInDifferentClassesIsSeparate() {
        EventLog log = new EventLog();
        MethodRecorder r = new MethodRecorder(log);
        r.sourceCompiled("A", "class A { void run() { } }");
        r.sourceCompiled("B", "class B { void run() { } }");
        assertEquals(2, log.countByType(EventType.METHOD_DEFINED));
