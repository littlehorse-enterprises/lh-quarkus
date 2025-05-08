package io.littlehorse.quarkus.runtime;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Unremovable
public class LHTaskWorkerRegister {

    private static final Logger log = LoggerFactory.getLogger(LHTaskWorkerRegister.class);
    private final List<LHTaskWorker> taskWorkers = Collections.synchronizedList(new ArrayList<>());
    private final LHRuntimeConfig config;

    public LHTaskWorkerRegister(LHRuntimeConfig config) {
        this.config = config;
    }

    public List<LHTaskWorker> getTaskWorkers() {
        return taskWorkers;
    }

    public void registerAndStartTaskWorker(LHTaskWorker worker) {
        if (config.tasksRegisterEnabled()) {
            log.info(
                    "Registering {}: {}",
                    LHTaskMethod.class.getSimpleName(),
                    worker.getTaskDefName());
            worker.registerTaskDef();
        }

        if (config.tasksStartEnabled()) {
            taskWorkers.add(worker);
            log.info(
                    "Starting {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
            worker.start();
        }
    }
}
