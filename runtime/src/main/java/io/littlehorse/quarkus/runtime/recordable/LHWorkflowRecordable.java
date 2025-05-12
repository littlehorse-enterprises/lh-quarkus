package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.runtime.register.LHWorkflowRegister;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHWorkflowRecordable {

    private final Class<?> beanClass;
    private final String wfSpecName;

    public LHWorkflowRecordable(Class<?> beanClass, String wfSpecName) {
        this.beanClass = beanClass;
        this.wfSpecName = wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public abstract void buildWorkflowThread(WorkflowThread workflowThread);

    public void registerWorkflow() {
        if (CDI.current().select(beanClass).isUnsatisfied()) return;
        LHWorkflowRegister register =
                CDI.current().select(LHWorkflowRegister.class).get();
        register.registerWorkflow(wfSpecName, this::buildWorkflowThread);
    }
}
