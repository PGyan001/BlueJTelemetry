package org.example.telemetry.adapter;

import org.example.telemetry.collector.CompileRecorder;
import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BlueJCompileAdapter implements CompileListener {
    private final CompileRecorder recorder;

    public BlueJCompileAdapter(CompileRecorder recorder) { this.recorder = recorder; }

    @Override
    public void compileStarted(CompileEvent event) { recorder.started(fileNames(event)); }

    @Override
    public void compileError(CompileEvent event) { recorder.error(fileNames(event), event.getErrorLineNumber(), event.getErrorMessage()); }

    @Override
    public void compileWarning(CompileEvent event) {}

    @Override
    public void compileSucceeded(CompileEvent event) {recorder.succeeded(fileNames(event)); }

    @Override
    public void compileFailed(CompileEvent event) {}

    private static List<String> fileNames(CompileEvent event) {
        File[] files = event.getFiles();
        if (files == null) { return List.of();}
        return Arrays.stream(files).map(File::getName).toList();
    }
}