package io.littlehorse.workflows;

import static io.littlehorse.tasks.PrintTask.PRINT_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import java.util.Map;

@LHWorkflow(ReactiveWorkflow.REACTIVE_WORKFLOW)
public class ReactiveWorkflow implements LHWorkflowDefinition {

    public static final String MESSAGE_VARIABLE = "message";
    public static final String REACTIVE_WORKFLOW = "reactive-workflow";
    public static final String NOTIFY_EVENT = "notify";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable message = wf.declareStr(MESSAGE_VARIABLE);
        wf.execute(PRINT_TASK, message);
        wf.throwEvent(NOTIFY_EVENT, message).registeredAs(Map.class);
    }
}
