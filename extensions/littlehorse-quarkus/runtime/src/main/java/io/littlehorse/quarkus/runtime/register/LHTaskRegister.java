package io.littlehorse.quarkus.runtime.register;

import io.littlehorse.quarkus.config.LHRuntimeConfig;
import io.littlehorse.quarkus.config.LHRuntimeConfig.TaskConfig;
import io.littlehorse.quarkus.runtime.LHTaskStatusesContainer;
import io.littlehorse.quarkus.runtime.health.LHTaskStatus;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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

    public void registerAndStartTask(String name, LHTaskWorker worker) {
        Optional<TaskConfig> taskConfig =
                Optional.ofNullable(config.specificTaskConfigs().get(name));
        boolean registerTask =
                taskConfig.map(TaskConfig::registerEnabled).orElse(config.tasksRegisterEnabled());
        boolean startTask =
                taskConfig.map(TaskConfig::startEnabled).orElse(config.tasksStartEnabled());

        if (registerTask) {
            log.info("Registering {}: {}", LHTaskMethod.class.getSimpleName(), name);
            worker.registerTaskDef();
        }

        if (startTask) {
            taskStatusesContainer.add(new LHTaskStatus(worker));
            log.info("Starting {}: {}", LHTaskMethod.class.getSimpleName(), name);
            worker.start();
        }
    }
}
