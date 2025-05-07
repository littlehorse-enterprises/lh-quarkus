package io.littlehorse.quarkus.workflow;

import io.littlehorse.sdk.wfsdk.WorkflowThread;

import java.util.function.Consumer;

@FunctionalInterface
public interface LHWorkflowConsumer extends Consumer<WorkflowThread> {}
