package io.littlehorse.tasks;

import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskMethod;

@LHTask
public class GreetingsTask {

    public static final String GREETINGS_TASK = "greetings";
    public static final String PRINT_TASK = "print";
    public static final String NAME_VARIABLE = "name";
    public static final String GREETINGS_WORKFLOW = "greetings";

    @LHWorkflow(GREETINGS_WORKFLOW)
    public void greetingsWorkflow(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(NAME_VARIABLE);
        TaskNodeOutput message = wf.execute(GREETINGS_TASK, name);
        wf.execute(PRINT_TASK, message);
    }

    @LHTaskMethod(GREETINGS_TASK)
    public String greetings(String name) {
        return "Hello %s".formatted(name);
    }

    @LHTaskMethod(PRINT_TASK)
    public void print(String message) {
        System.out.println(message);
    }
}
