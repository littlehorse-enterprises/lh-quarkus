package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;

public interface LHWorkflowRecordable {

    void buildWorkflowThread(WorkflowThread workflowThread);

    String getWfSpecName();
}
