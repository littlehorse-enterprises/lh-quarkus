package io.littlehorse.workflows;

import io.littlehorse.forms.ApprovalResult;
import io.littlehorse.forms.ApproveUserTask;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow("user-task-approval")
public class UserTaskApprovalWorkflow implements LHWorkflowDefinition {
    @Override
    public void define(WorkflowThread wf) {
        var userId = wf.declareStr("user-id");
        var approval = wf.declareStruct("approval", ApprovalResult.class);
        var approved = wf.declareBool("approved");
        var output = wf.assignUserTask(ApproveUserTask.APPROVE_USER_TASK, userId, null);
        approval.assign(output);
        approved.assign(output.get("isApproved"));
    }
}
