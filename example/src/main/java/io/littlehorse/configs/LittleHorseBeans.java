package io.littlehorse.configs;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.workers.GreetingWorker;
import io.littlehorse.workers.PrinterWorker;
import io.smallrye.common.annotation.Identifier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

public class LittleHorseBeans {

    public static final String TASK_GREETINGS = "greetings";
    public static final String WF_GREETINGS = "greetings";
    public static final String TASK_PRINT = "print";
    public static final String VAR_NAME = "name";

    @Produces
    @ApplicationScoped
    Workflow workflow() {
        return Workflow.newWorkflow(WF_GREETINGS, thread -> {
            TaskNodeOutput output = thread.execute(TASK_GREETINGS, thread.declareStr(VAR_NAME));
            thread.execute(TASK_PRINT, output);
        });
    }

    @Produces
    @ApplicationScoped
    @Identifier(TASK_GREETINGS)
    LHTaskWorker workerGreeting(LHConfig config) {
        return new LHTaskWorker(new GreetingWorker(), TASK_GREETINGS, config);
    }

    @Produces
    @ApplicationScoped
    @Identifier(TASK_PRINT)
    LHTaskWorker workerPrinter(LHConfig config) {
        return new LHTaskWorker(new PrinterWorker(), TASK_PRINT, config);
    }
}
