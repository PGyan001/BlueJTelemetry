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
        r.classStateChanged("P", "Dog", false);
        r.classStateChanged("P", "Dog", true);   

        assertEquals(1, log.countByType(EventType.CLASS_CREATED));
        assertEquals(2, log.countByType(EventType.CLASS_MODIFIED));
        assertEquals(1, log.countByType(EventType.EDIT));
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
        r.projectOpened("P", false);
        r.projectClosing(null);
        assertEquals(1, log.countByType(EventType.PROJECT_OPENED));
        assertEquals(1, log.countByType(EventType.PROJECT_CLOSED));
    }

    @Test
    void classesAnnouncedBeforeProjectNameAreNotCreations() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);

        // real BlueJ order when reopening a project: class events (no project name) come first
        r.classStateChanged("", "Dog", true);
        r.projectOpened("P", false);
        r.classStateChanged("P", "Dog", false);

        assertEquals(0, log.countByType(EventType.CLASS_CREATED));
        assertEquals(1, log.countByType(EventType.CLASS_MODIFIED));
    }

    @Test
    void reopenedClassesDoNotSuppressCreationInAnotherProject() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);

        r.classStateChanged("", "Dog", true);   // project A reopened, Dog never edited
        r.projectOpened("A", false);
        r.projectClosing("A");

        r.projectOpened("B", true);
        r.classStateChanged("B", "Dog", true);  // brand new class that happens to share the name

        assertEquals(1, log.countByType(EventType.CLASS_CREATED));
        assertEquals(0, log.countByType(EventType.CLASS_MODIFIED));
    }

    @Test
    void closingAProjectDiscardsUnclaimedAnnouncements() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.classStateChanged("", "Ghost", true);
        r.projectClosing("X");
        r.projectOpened("Y", false);
        r.classStateChanged("Y", "Ghost", true);
        assertEquals(1, log.countByType(EventType.CLASS_CREATED));
    }

    @Test
    void renamedClassSettlesBeforeNextRealEdit() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.classStateChanged("P", "Cat", true);
        r.classRenamed("P", "Cat", "Cat2");
        r.classStateChanged("P", "Cat2", false);  // BlueJ's template change after rename: swallowed
        r.classStateChanged("P", "Cat2", false);  // genuine edit

        assertEquals(1, log.countByType(EventType.EDIT));
    }

    @Test
    void rejectsNullSink() {
        assertThrows(NullPointerException.class, () -> new LifecycleRecorder(null));
    }

    @Test
    void newlyCreatedProjectIsRecordedAsCreated() {
        EventLog log = new EventLog();
        LifecycleRecorder r = new LifecycleRecorder(log);
        r.projectOpened("P", true);
        r.projectOpened("Q", false);
        assertEquals(1, log.countByType(EventType.PROJECT_CREATED));
        assertEquals(1, log.countByType(EventType.PROJECT_OPENED));
    }
}