package io.littlehorse.configs;

import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.Workflow;

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

    //    @Produces
    //    @ApplicationScoped
    //    @Identifier(TASK_GREETINGS)
    //    LHTaskWorker workerGreetings(LHConfig config) {
    //        return new LHTaskWorker(new GreetingsTask(), TASK_GREETINGS, config);
    //    }
    //
    //    @Produces
    //    @ApplicationScoped
    //    @Identifier(TASK_PRINT)
    //    LHTaskWorker workerPrinter(LHConfig config) {
    //        return new LHTaskWorker(new PrinterTask(), TASK_PRINT, config);
    //    }
}
