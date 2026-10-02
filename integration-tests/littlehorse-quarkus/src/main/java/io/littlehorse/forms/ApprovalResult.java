package io.littlehorse.forms;

import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHStructField;

@LHStructDef("${struct.approval.name}")
public class ApprovalResult {
    private Boolean approved;

    @LHStructField(name = "isApproved", description = "Whether the request is approved.")
    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
