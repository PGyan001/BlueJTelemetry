package org.example.telemetry.adapter;

import org.example.telemetry.collector.CompileRecorder;
import org.example.telemetry.collector.MethodRecorder;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class BlueJCompileAdapter implements CompileListener {
    private final CompileRecorder recorder;
    private final MethodRecorder methodRecorder;

    public BlueJCompileAdapter(CompileRecorder recorder, MethodRecorder methodRecorder) {
        this.recorder = recorder;
        this.methodRecorder = methodRecorder;
    }

    @Override
    public void compileStarted(CompileEvent event) { recorder.started(fileNames(event)); }

    @Override
    public void compileError(CompileEvent event) { recorder.error(fileNames(event), event.getErrorLineNumber(), event.getErrorMessage()); }

    @Override
    public void compileWarning(CompileEvent event) {}

    @Override
    public void compileSucceeded(CompileEvent event) {
        recorder.succeeded(fileNames(event));
        File[] files = event.getFiles();
        if (files == null) { return; }
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".java")) { continue; }
            try {
                methodRecorder.sourceCompiled(name.substring(0, name.length() - 5), Files.readString(file.toPath()));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void compileFailed(CompileEvent event) {}

    private static List<String> fileNames(CompileEvent event) {
        File[] files = event.getFiles();
        if (files == null) { return List.of();}
        return Arrays.stream(files).map(File::getName).toList();
    }
}
