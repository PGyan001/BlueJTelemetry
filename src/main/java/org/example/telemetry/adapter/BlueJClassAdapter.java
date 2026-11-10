package org.example.telemetry.adapter;

import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;
import org.example.telemetry.collector.LifecycleRecorder;

public class BlueJClassAdapter implements ClassListener {

    private final LifecycleRecorder recorder;

    public BlueJClassAdapter(LifecycleRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void classStateChanged(ClassEvent event) {
        recorder.classStateChanged(
                AdapterNames.project(event.getPackage()),
                AdapterNames.className(event),
                event.isClassCompiled());
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        recorder.classRenamed(
                AdapterNames.project(event.getPackage()),
                event.getOldName(),
                AdapterNames.className(event));
    }

    @Override
    public void classRemoved(ClassEvent event) {
        recorder.classRemoved(
                AdapterNames.project(event.getPackage()),
                AdapterNames.className(event));
    }
}