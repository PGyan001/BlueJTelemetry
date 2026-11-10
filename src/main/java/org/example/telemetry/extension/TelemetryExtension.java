package org.example.telemetry.extension;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

import org.example.telemetry.adapter.BlueJClassAdapter;
import org.example.telemetry.adapter.BlueJCompileAdapter;
import org.example.telemetry.adapter.BlueJInvocationAdapter;
import org.example.telemetry.adapter.BlueJPackageAdapter;
import org.example.telemetry.collector.CompileRecorder;
import org.example.telemetry.collector.InvocationRecorder;
import org.example.telemetry.collector.LifecycleRecorder;
import org.example.telemetry.events.EventLog;

public class TelemetryExtension extends Extension{

    private final EventLog eventLog = new EventLog();

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() >= 3;
    }

    @Override
    public void startup(BlueJ bluej) {
        LifecycleRecorder lifecycle = new LifecycleRecorder(eventLog);
        bluej.addPackageListener(new BlueJPackageAdapter(lifecycle));
        bluej.addClassListener(new BlueJClassAdapter(lifecycle));
        System.out.println("[BlueJTelemetry] Extension started");
    }

    @Override
    public String getName() {
        return "Telemetry";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    public EventLog getEventLog() {
        return eventLog;
    }
}
