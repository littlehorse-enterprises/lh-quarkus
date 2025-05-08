package io.littlehorse.quarkus.workflow;

import io.littlehorse.sdk.wfsdk.WorkflowThread;

@FunctionalInterface
public interface LHWorkflowDefinition {
    void define(WorkflowThread wf);
}
