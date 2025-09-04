package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.smallrye.config.SmallRyeConfig;

import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.config.ConfigProvider;

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
        SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        // config.getValue()

        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();

        Workflow workflow = Workflow.newWorkflow(getName(), this::buildWorkflowThread);
        if (parent != null) {
            workflow.setParent(parent);
        }

        //        workflow.setDefaultTaskExponentialBackoffPolicy();
        //        workflow.setDefaultTaskRetries();
        //        workflow.setDefaultTaskTimeout();
        //
        //        workflow.withRetentionPolicy();
        //        workflow.withDefaultThreadRetentionPolicy();
        //        workflow.withUpdateType()

        register.registerWorkflow(workflow);
    }
}
