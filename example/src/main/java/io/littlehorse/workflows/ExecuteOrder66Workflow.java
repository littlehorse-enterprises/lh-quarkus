package io.littlehorse.workflows;

import static io.littlehorse.workflows.ApproveForm.APPROVE_USER_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.common.proto.Comparator;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.UserTaskOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.workers.PrinterTask;

@LHWorkflow("execute-order-66")
public class ExecuteOrder66Workflow implements ThreadFunc {

    @Override
    public void threadFunction(WorkflowThread wf) {
        WfRunVariable executor = wf.declareStr("executor");
        WfRunVariable isApproved = wf.declareBool("approved");

        UserTaskOutput userTaskOutput =
                wf.assignUserTask(APPROVE_USER_TASK, executor, null).withNotes("Execute Order 66");
        isApproved.assign(userTaskOutput.jsonPath("$.isApproved"));

        wf.doIfElse(
                wf.condition(isApproved, Comparator.EQUALS, true),
                ifBody -> ifBody.execute(
                        PrinterTask.TASK_PRINT, wf.format("Well done {0}", executor)),
                elseBody -> elseBody.execute(
                        PrinterTask.TASK_PRINT, wf.format("Very bad {0}", executor)));
    }
}
