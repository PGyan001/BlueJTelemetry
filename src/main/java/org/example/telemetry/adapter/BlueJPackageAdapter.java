package org.example.telemetry.adapter;

import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;
import org.example.telemetry.collector.LifecycleRecorder;

public class BlueJPackageAdapter implements PackageListener{
    private final LifecycleRecorder recorder;

    public BlueJPackageAdapter(LifecycleRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void packageOpened(PackageEvent event) {
        recorder.projectOpened(AdapterNames.project(event.getPackage()));
    }

    @Override
    public void packageClosing(PackageEvent event) {
        recorder.projectClosing(AdapterNames.project(event.getPackage()));
    }
}
