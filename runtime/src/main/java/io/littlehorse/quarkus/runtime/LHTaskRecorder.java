package io.littlehorse.quarkus.runtime;

import io.littlehorse.sdk.common.config.LHConfig;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class LHTaskRecorder {
    private final LHConfig config;

    public LHTaskRecorder(LHConfig config) {
        this.config = config;
    }
}
