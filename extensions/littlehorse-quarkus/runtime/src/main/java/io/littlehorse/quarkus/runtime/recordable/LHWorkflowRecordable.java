package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigExpression;
import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable extends LHRecordable {

    private final String parent;

    public LHWorkflowRecordable(Class<?> beanClass, String name, String parent) {
        super(beanClass, name);
        this.parent = parent;
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

        //        workflow.setDefaultTaskExponentialBackoffPolicy();
        //                workflow.setDefaultTaskRetries();
        //        workflow.setDefaultTaskTimeout();
        //
        //        workflow.withRetentionPolicy();
        //        workflow.withDefaultThreadRetentionPolicy();
        //        workflow.withUpdateType()

        register.registerWorkflow(workflow);
    }
}
