package io.littlehorse.forms;

import io.littlehorse.quarkus.task.LHUserTaskDef;

@LHUserTaskDef(value = ApproveUserTask.APPROVE_USER_TASK, result = ApprovalResult.class)
public class ApproveUserTask {
    public static final String APPROVE_USER_TASK = "approve-user-task";
}
