package org.example.telemetry.collector;

import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleRecorderTest {

    @Test
    void firstSightingIsCreationThenModification() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);

        r.classStateChanged("P", "Dog", false);
        r.classStateChanged("P", "Dog", false);
        r.classStateChanged("P", "Dog", true);

        assertEquals(1, log.countByType(EventType.CLASS_CREATED));
        assertEquals(2, log.countByType(EventType.CLASS_MODIFIED));
        assertEquals(1, log.countByType(EventType.EDIT)); // only the uncompiled modification
    }

    @Test
    void sameClassNameInDifferentProjectsIsSeparate() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.classStateChanged("A", "Dog", true);
        r.classStateChanged("B", "Dog", true);
        assertEquals(2, log.countByType(EventType.CLASS_CREATED));
    }

    @Test
    void renameUpdatesKnownClasses() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.classStateChanged("P", "Dog", true);
        r.classRenamed("P", "Dog", "Puppy");
        r.classStateChanged("P", "Puppy", true);

        assertEquals(1, log.countByType(EventType.CLASS_RENAMED));
        assertEquals(1, log.countByType(EventType.CLASS_MODIFIED));
    }

    @Test
    void removedClassIsCreatedAgainIfReadded() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.classStateChanged("P", "Dog", true);
        r.classRemoved("P", "Dog");
        r.classStateChanged("P", "Dog", true);

        assertEquals(1, log.countByType(EventType.CLASS_DELETED));
        assertEquals(2, log.countByType(EventType.CLASS_CREATED));
    }

    @Test
    void projectEventsAreRecorded() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.projectOpened("P");
        r.projectClosing(null);
        assertEquals(1, log.countByType(EventType.PROJECT_OPENED));
        assertEquals(1, log.countByType(EventType.PROJECT_CLOSED));
    }

    @Test
    void rejectsNullSink() {
        assertThrows(NullPointerException.class, () -> new LifecycleRecorder(null));
    }
}