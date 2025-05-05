package io.littlehorse.workflows;

import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.sdk.usertask.annotations.UserTaskField;

@LHUserTaskForm(ApproveForm.APPROVE_USER_TASK)
public class ApproveForm {

    public static final String APPROVE_USER_TASK = "approve-user-task";

    @UserTaskField(
            displayName = "Approved?",
            description = "Reply 'true' if this is an acceptable request.")
    public boolean isApproved;
}
