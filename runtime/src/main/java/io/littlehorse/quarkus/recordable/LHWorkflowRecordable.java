package io.littlehorse.quarkus.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;

public interface LHWorkflowRecordable {

    /**
     * Invoke it only from the recorder class.
     *
     * @param workflowThread Invoke it inside a ThreadFunc.
     */
    void getWorkflowThread(WorkflowThread workflowThread);

    String getWfSpecName();
}
