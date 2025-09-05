package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigExpression;
import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.common.proto.AllowedUpdateType;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable extends LHRecordable {

    private final String parent;
    private final String defaultTaskTimeout;
    private final String defaultTaskRetries;
    private final String updateType;

    public LHWorkflowRecordable(
            Class<?> beanClass,
            String name,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType) {
        super(beanClass, name);
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
    }

    public String getDefaultTaskRetries() {
        return defaultTaskRetries;
    }

    public String getDefaultTaskTimeout() {
        return defaultTaskTimeout;
    }

    public String getParent() {
        return parent;
    }

    public abstract void buildWorkflowThread(WorkflowThread workflowThread);

    public void registerWorkflow() {
        if (!exists()) return;

        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();

        Workflow workflow = Workflow.newWorkflow(
                ConfigExpression.expand(getName()).asString(), this::buildWorkflowThread);

        if (parent != null) {
            workflow.setParent(ConfigExpression.expand(parent).asString());
        }

        if (defaultTaskTimeout != null) {
            workflow.setDefaultTaskTimeout(
                    ConfigExpression.expand(defaultTaskTimeout).asInt());
        }

        if (defaultTaskRetries != null) {
            workflow.setDefaultTaskRetries(
                    ConfigExpression.expand(defaultTaskRetries).asInt());
        }

        if (updateType != null) {
            workflow.withUpdateType(AllowedUpdateType.valueOf(
                    ConfigExpression.expand(defaultTaskRetries).asString().toUpperCase()));
        }

        //                workflow.setDefaultTaskExponentialBackoffPolicy();
        //                workflow.withRetentionPolicy();
        //                workflow.withDefaultThreadRetentionPolicy();

        register.registerWorkflow(workflow);
    }
}
