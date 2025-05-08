package io.littlehorse.workflows;

import static io.littlehorse.tasks.GreetingsTask.TASK_GREETINGS;
import static io.littlehorse.tasks.PrintTask.TASK_PRINT;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowConsumer;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow(GreetingsWorkflow.WF_GREETINGS)
public class GreetingsWorkflow implements LHWorkflowConsumer {

    public static final String VAR_NAME = "name";
    public static final String WF_GREETINGS = "greetings";

    @Override
    public void accept(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(VAR_NAME);
        TaskNodeOutput message = wf.execute(TASK_GREETINGS, name);
        wf.execute(TASK_PRINT, message);
    }
}
