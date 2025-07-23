package io.littlehorse.quarkus.runtime.register;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.littlehorse.quarkus.runtime.health.LHTaskStatus;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Unremovable
public class LHTaskRegister {

    private static final Logger log = LoggerFactory.getLogger(LHTaskRegister.class);
    private final LHRuntimeConfig config;
    private final LHTaskStatusesContainer taskStatusesContainer;

    public LHTaskRegister(LHRuntimeConfig config, LHTaskStatusesContainer taskStatusesContainer) {
        this.config = config;
        this.taskStatusesContainer = taskStatusesContainer;
    }

    public void registerAndStartTask(LHTaskWorker worker) {
        if (config.tasksRegisterEnabled()) {
            log.info(
                    "Registering {}: {}",
                    LHTaskMethod.class.getSimpleName(),
                    worker.getTaskDefName());
            worker.registerTaskDef();
        }

        if (config.tasksStartEnabled()) {
            taskStatusesContainer.add(new LHTaskStatus(worker));
            log.info(
                    "Starting {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
            worker.start();
        }
    }
}
