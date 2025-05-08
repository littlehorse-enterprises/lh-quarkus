package io.littlehorse.quarkus.runtime;

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
public class LHTaskWorkerContainer {

    private static final Logger log = LoggerFactory.getLogger(LHTaskWorkerContainer.class);
    public List<LHTaskWorker> taskWorkers = Collections.synchronizedList(new ArrayList<>());

    public List<LHTaskWorker> getTaskWorkers() {
        return taskWorkers;
    }

    public void startTaskWorker(LHTaskWorker worker) {
        taskWorkers.add(worker);

        log.info("Registering {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
        worker.registerTaskDef();

        log.info("Starting {}: {}", LHTaskMethod.class.getSimpleName(), worker.getTaskDefName());
        worker.start();
    }
}
