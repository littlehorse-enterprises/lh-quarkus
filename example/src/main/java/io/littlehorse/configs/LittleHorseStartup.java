package io.littlehorse.configs;

import static io.littlehorse.configs.LittleHorseBeans.TASK_COUNTER;
import static io.littlehorse.configs.LittleHorseBeans.TASK_GREETINGS;
import static io.littlehorse.configs.LittleHorseBeans.TASK_PRINT;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Identifier;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LittleHorseStartup {

    private static final Logger log = LoggerFactory.getLogger(LittleHorseStartup.class);

    final Workflow workflow;
    final LHConfig config;
    final LHTaskWorker taskGreetings;
    final LHTaskWorker taskPrint;
    private final LHTaskWorker taskCounter;

    public LittleHorseStartup(
            LHConfig config,
            Workflow workflow,
            @Identifier(TASK_GREETINGS) LHTaskWorker taskGreetings,
            @Identifier(TASK_COUNTER) LHTaskWorker taskCounter,
            @Identifier(TASK_PRINT) LHTaskWorker taskPrint) {
        this.workflow = workflow;
        this.taskGreetings = taskGreetings;
        this.config = config;
        this.taskCounter = taskCounter;
        this.taskPrint = taskPrint;
    }

    @Startup
    void startup() {
        log.info("Registering tasks");
        taskGreetings.registerTaskDef();
        taskPrint.registerTaskDef();
        taskCounter.registerTaskDef();

        log.info("Starting workers");
        taskGreetings.start();
        taskPrint.start();
        taskCounter.start();

        log.info("Registering workflow");
        workflow.registerWfSpec(config.getBlockingStub());
    }

    @Shutdown
    void shutdown() {
        log.info("Stopping workers");
        taskGreetings.close();
        taskPrint.close();
        taskCounter.close();
    }
}
