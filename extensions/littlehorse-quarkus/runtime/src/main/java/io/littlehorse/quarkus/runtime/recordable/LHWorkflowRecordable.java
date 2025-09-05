package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigExpression;
import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable extends LHRecordable {

    private final String parent;
    private final String defaultTaskTimeout;

    public LHWorkflowRecordable(
            Class<?> beanClass, String name, String parent, String defaultTaskTimeout) {
        super(beanClass, name);
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
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

        Workflow workflow = Workflow.newWorkflow(getName(), this::buildWorkflowThread);

        if (parent != null) {
            workflow.setParent(ConfigExpression.expand(parent).asString());
        }

        if (defaultTaskTimeout != null) {
            workflow.setDefaultTaskTimeout(
                    ConfigExpression.expand(defaultTaskTimeout).asInt());
        }

        //        workflow.setDefaultTaskExponentialBackoffPolicy();
        //        workflow.setDefaultTaskRetries();
        //
        //        workflow.withRetentionPolicy();
        //        workflow.withDefaultThreadRetentionPolicy();
        //        workflow.withUpdateType()

        register.registerWorkflow(workflow);
    }
}
