package io.littlehorse.quarkus.runtime.health;

import static io.littlehorse.sdk.worker.LHTaskWorkerHealthReason.UNHEALTHY;

import io.littlehorse.sdk.worker.LHTaskWorker;

public class LHTaskStatus {
    private final LHTaskWorker taskWorker;

    public LHTaskStatus(LHTaskWorker taskWorker) {
        this.taskWorker = taskWorker;
    }

    public String getTaskDefName() {
        return taskWorker.getTaskDefName();
    }

    public boolean isHealthy() {
        return !UNHEALTHY.equals(taskWorker.healthStatus().getReason());
    }
}
