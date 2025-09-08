package io.littlehorse.quarkus.runtime.recordable;

import io.quarkus.runtime.annotations.RecordableConstructor;

public class LHExponentialBackoffRetryRecordable {

    private final String baseIntervalMs;
    private final String multiplier;
    private final String maxDelayMs;

    @RecordableConstructor
    public LHExponentialBackoffRetryRecordable(
            String baseIntervalMs, String multiplier, String maxDelayMs) {
        this.baseIntervalMs = baseIntervalMs;
        this.multiplier = multiplier;
        this.maxDelayMs = maxDelayMs;
    }

    public String getBaseIntervalMs() {
        return baseIntervalMs;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public String getMaxDelayMs() {
        return maxDelayMs;
    }
}
