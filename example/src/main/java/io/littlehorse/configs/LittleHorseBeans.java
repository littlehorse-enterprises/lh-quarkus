package io.littlehorse.configs;

import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

public class LittleHorseBeans {

    public static final String TASK_GREETINGS = "greetings";
    public static final String TASK_PRINT = "print";
    public static final String TASK_COUNTER = "counter";
    public static final String WF_GREETINGS = "greetings";
    public static final String VAR_NAME = "name";

    @Produces
    @ApplicationScoped
    Workflow workflow() {
        return Workflow.newWorkflow(WF_GREETINGS, thread -> {
            WfRunVariable name = thread.declareStr(VAR_NAME);
            TaskNodeOutput totalCount = thread.execute(TASK_COUNTER);
            TaskNodeOutput greeting = thread.execute(TASK_GREETINGS, name);
            thread.execute(TASK_PRINT, totalCount, greeting);
        });
    }
}
