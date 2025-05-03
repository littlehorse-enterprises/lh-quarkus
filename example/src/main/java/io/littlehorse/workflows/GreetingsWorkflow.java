package io.littlehorse.workflows;

import static io.littlehorse.workers.GreetingsTask.TASK_GREETINGS;
import static io.littlehorse.workers.PrinterTask.TASK_PRINT;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow(GreetingsWorkflow.WF_GREETINGS)
public class GreetingsWorkflow implements ThreadFunc {

    public static final String VAR_NAME = "name";
    public static final String WF_GREETINGS = "greetings";

    @Override
    public void threadFunction(WorkflowThread thread) {
        WfRunVariable name = thread.declareStr(VAR_NAME);
        TaskNodeOutput message = thread.execute(TASK_GREETINGS, name);
        thread.execute(TASK_PRINT, message);
    }
}
