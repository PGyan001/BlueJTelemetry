package org.example.telemetry.extension;

import bluej.extensions2.BlueJ;
import bluej.extensions2.Extension;

import org.example.telemetry.events.EventLog;

import java.net.MalformedURLException;
import java.net.URL;

public class TelemetryExtension extends Extension{

    private final EventLog eventLog = new EventLog();

    @Override
    public boolean isCompatible() {
        return true;
    }

    @Override
    public void startup(BlueJ blueJ) {
        System.out.println("[App] Extension started");
    }

    @Override
    public String getName() {
        return "Telemetry App";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public EventLog getEventLog() {
        return eventLog;
    }
}
