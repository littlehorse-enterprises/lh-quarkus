package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable extends LHRecordable {

    public LHWorkflowRecordable(Class<?> beanClass, String name) {
        super(beanClass, name);
    }

    public abstract void buildWorkflowThread(WorkflowThread workflowThread);

    public void registerWorkflow() {
        if (!exists()) return;

        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();
        register.registerWorkflow(getName(), this::buildWorkflowThread);
    }
}
