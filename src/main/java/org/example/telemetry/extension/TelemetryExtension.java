package org.example.telemetry.extension;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

import org.example.telemetry.events.EventLog;

public class TelemetryExtension extends Extension{

    private final EventLog eventLog = new EventLog();

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() >= 3;
    }

    @Override
    public void startup(BlueJ blueJ) {
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
