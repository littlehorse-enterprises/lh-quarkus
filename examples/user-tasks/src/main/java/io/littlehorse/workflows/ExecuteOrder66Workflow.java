package io.littlehorse.workflows;

import static io.littlehorse.forms.ApproveForm.APPROVE_USER_TASK;

import io.littlehorse.proxy.dev.PrintTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.common.proto.Comparator;
import io.littlehorse.sdk.wfsdk.UserTaskOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow("execute-order-66")
public class ExecuteOrder66Workflow implements LHWorkflowDefinition {

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable executor = wf.declareStr("executor");
        WfRunVariable isApproved = wf.declareBool("approved");

        UserTaskOutput userTaskOutput =
                wf.assignUserTask(APPROVE_USER_TASK, executor, null).withNotes("Execute Order 66");
        isApproved.assign(userTaskOutput.jsonPath("$.isApproved"));

        wf.doIfElse(
                wf.condition(isApproved, Comparator.EQUALS, true),
                ifBody ->
                        ifBody.execute(PrintTask.PRINT_TASK, wf.format("Well done {0}", executor)),
                elseBody -> elseBody.execute(
                        PrintTask.PRINT_TASK, wf.format("Very bad {0}", executor)));
    }
}
