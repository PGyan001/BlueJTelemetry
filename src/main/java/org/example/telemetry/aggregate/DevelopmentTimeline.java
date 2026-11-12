package org.example.telemetry.aggregate;

import org.example.telemetry.events.EventType;
import org.example.telemetry.events.TelemetryEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** Class creations, renames, deletions and method definitions in time order. */
public class DevelopmentTimeline {

    public record Entry(Instant time, String kind, String className, String detail) {}

    private static final Set<EventType> RELEVANT = Set.of(EventType.CLASS_CREATED,
            EventType.CLASS_RENAMED, EventType.CLASS_DELETED, EventType.METHOD_DEFINED);

    public List<Entry> build(List<TelemetryEvent> events) {
        List<Entry> entries = new ArrayList<>();
        for (TelemetryEvent e : events) {
            if (!RELEVANT.contains(e.type())) {
                continue;
            }
            String cls = e.details().getOrDefault("class", "");
            switch (e.type()) {
                case CLASS_CREATED -> entries.add(new Entry(e.timestamp(), "Class created", cls, ""));
                case CLASS_RENAMED -> entries.add(new Entry(e.timestamp(), "Class renamed", cls,
                        "was " + e.details().getOrDefault("oldName", "")));
                case CLASS_DELETED -> entries.add(new Entry(e.timestamp(), "Class deleted", cls, ""));
