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
import org.example.telemetry.events.CompositeSink;
import org.example.telemetry.events.EventLog;
import org.example.telemetry.events.EventSink;
import org.example.telemetry.export.SessionExporter;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class TelemetryExtension extends Extension{

    private final EventLog eventLog = new EventLog();
    private final Instant sessionStart = Instant.now();
    private final String sessionId = UUID.randomUUID().toString();

    @Override
    public boolean isCompatible() {
        return getExtensionsAPIVersionMajor() >= 3;
    }

    @Override
    public void startup(BlueJ bluej) {
        EventSink sink = new CompositeSink(eventLog,
                new FileDebugSink(Path.of(System.getProperty("user.home"), "telemetry-debug.txt")));

        Path outputDir = bluej.getUserConfigDir().toPath().resolve("telemetry");
        String studentId = bluej.getExtensionPropertyString("studentId", "STUDENT_ID_PLACEHOLDER");
        SessionExporter exporter = new SessionExporter(eventLog, outputDir, studentId, sessionId, sessionStart);

        Runnable autoExport = () -> {
            try {
                exporter.exportJson();
            } catch (Exception e) {
                System.err.println("[Telemetry] Auto-export failed: " + e);
            }
        };

        LifecycleRecorder lifecycle = new LifecycleRecorder(sink);
        bluej.addPackageListener(new BlueJPackageAdapter(lifecycle, autoExport));
        bluej.addClassListener(new BlueJClassAdapter(lifecycle));
        bluej.addCompileListener(new BlueJCompileAdapter(new CompileRecorder(sink)));
        bluej.addInvocationFinishedListener(new BlueJInvocationAdapter(new InvocationRecorder(sink)));
        bluej.setMenuGenerator(new TelemetryMenuGenerator(exporter));
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
