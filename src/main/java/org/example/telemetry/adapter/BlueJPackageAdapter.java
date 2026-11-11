package org.example.telemetry.adapter;

import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;
import org.example.telemetry.collector.LifecycleRecorder;

public class BlueJPackageAdapter implements PackageListener {

    private final LifecycleRecorder recorder;
    private final Runnable onClosing;

    public BlueJPackageAdapter(LifecycleRecorder recorder, Runnable onClosing) {
        this.recorder = recorder;
        this.onClosing = onClosing;
    }

    @Override
    public void packageOpened(PackageEvent event) {
        recorder.projectOpened(AdapterNames.project(event.getPackage()),
                AdapterNames.isNewlyCreated(event.getPackage()));
    }

    @Override
    public void packageClosing(PackageEvent event) {
        recorder.projectClosing(AdapterNames.project(event.getPackage()));
        try {
            onClosing.run();
        } catch (RuntimeException e) {
            // never let telemetry break BlueJ
        }
    }
}