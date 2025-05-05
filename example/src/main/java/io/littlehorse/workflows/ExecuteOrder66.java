package io.littlehorse.workflows;

import static io.littlehorse.workflows.ApproveForm.APPROVE_USER_TASK;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.wfsdk.ThreadFunc;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow("execute-order-66")
public class ExecuteOrder66 implements ThreadFunc {

    @Override
    public void threadFunction(WorkflowThread thread) {
        thread.assignUserTask(APPROVE_USER_TASK, "Anakin", null).withNotes("Execute Order 66");
    }
}
