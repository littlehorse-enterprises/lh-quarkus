package io.littlehorse.configs;

import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;
import static io.littlehorse.configs.LittleHorseBeans.TASK_PRINT;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Identifier;

import jakarta.enterprise.context.ApplicationScoped;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class LittleHorseStartup {

    final Workflow workflow;
    final LHConfig config;
    final LHTaskWorker taskGreetings;
    final LHTaskWorker taskPrint;

    public LittleHorseStartup(
            LHConfig config,
            Workflow workflow,
            @Identifier(TASK_GREETINGS) LHTaskWorker taskGreetings,
            @Identifier(TASK_PRINT) LHTaskWorker taskPrint) {
        this.workflow = workflow;
        this.taskGreetings = taskGreetings;
        this.config = config;
        this.taskPrint = taskPrint;
    }

    @Startup
    void startup() {
        log.info("Registering tasks");
        taskGreetings.registerTaskDef();
        taskPrint.registerTaskDef();

        log.info("Starting workers");
        taskGreetings.start();
        taskPrint.start();

        log.info("Registering workflow");
        workflow.registerWfSpec(config.getBlockingStub());
    }

    @Shutdown
    void shutdown() {
        log.info("Stopping workers");
        taskGreetings.close();
        taskPrint.close();
    }
}
