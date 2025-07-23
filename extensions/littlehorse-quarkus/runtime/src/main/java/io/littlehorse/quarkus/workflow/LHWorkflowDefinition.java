package io.littlehorse.quarkus.workflow;

import io.littlehorse.sdk.wfsdk.WorkflowThread;

@FunctionalInterface
public interface LHWorkflowDefinition {
    /**
     * Define a WfSpec
     *
     * @param wf WfSpec entrypoint thread.
     */
    void define(WorkflowThread wf);
}
