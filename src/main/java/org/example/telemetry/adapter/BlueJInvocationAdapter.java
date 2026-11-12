package org.example.telemetry.adapter;

import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;
import org.example.telemetry.collector.ExitStatus;
import org.example.telemetry.collector.InvocationRecorder;

public class BlueJInvocationAdapter implements InvocationFinishedListener {
    private final InvocationRecorder recorder;
    public BlueJInvocationAdapter(InvocationRecorder recorder) { this.recorder = recorder;}

    @Override
    public void invocationFinished(InvocationFinishedEvent event) {
        Object result = event.getResult();
        recorder.finished(
            event.getClassName(),
            event.getMethodName(),
            event.getObjectName(),
            event.getParameters(),
            toStatus(event.getEventType()),
            result == null ? "" : result.toString());
    }

    static ExitStatus toStatus(InvocationFinishedEvent.EventType type) {
        if (type == null) {return ExitStatus.UNKNOWN; }
        return switch (type) {
            case NORMAL_EXIT -> ExitStatus.NORMAL;
            case EXCEPTION_EXIT -> ExitStatus.EXCEPTION;
            case TERMINATED_EXIT -> ExitStatus.TERMINATED;
            case UNKNOWN_EXIT -> ExitStatus.UNKNOWN;
        };
    }
}