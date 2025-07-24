package io.littlehorse.workflows;

import static io.littlehorse.tasks.GreetingsTask.GREETINGS_TASK;
import static io.littlehorse.tasks.PrintTask.PRINT_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow(GreetingsWorkflow.GREETINGS_WORKFLOW)
public class GreetingsWorkflow implements LHWorkflowDefinition {

    public static final String NAME_VARIABLE = "name";
    public static final String GREETINGS_WORKFLOW = "greetings";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr(NAME_VARIABLE);
        TaskNodeOutput message = wf.execute(GREETINGS_TASK, name);
        wf.execute(PRINT_TASK, message);
    }
}
