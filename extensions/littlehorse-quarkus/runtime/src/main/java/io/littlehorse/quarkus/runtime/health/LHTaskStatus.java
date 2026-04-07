package io.littlehorse.quarkus.runtime.health;

import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.sdk.worker.LHTaskWorkerHealthReason;

public class LHTaskStatus {
    private final LHTaskWorker taskWorker;

    public LHTaskStatus(LHTaskWorker taskWorker) {
        this.taskWorker = taskWorker;
    }

    public String getTaskDefName() {
        return taskWorker.getTaskDefName();
    }

    public LHTaskWorkerHealthReason getStatus() {
        return taskWorker.healthStatus().getReason();
    }
}
