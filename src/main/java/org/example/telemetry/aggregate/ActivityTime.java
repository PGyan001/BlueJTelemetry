package org.example.telemetry.aggregate;

import java.time.Duration;

public record ActivityTime(Duration active, Duration idle) {
    public Duration total() {
        return active.plus(idle);
    }
}
