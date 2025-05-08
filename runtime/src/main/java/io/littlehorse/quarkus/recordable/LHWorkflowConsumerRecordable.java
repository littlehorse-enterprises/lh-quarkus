package io.littlehorse.quarkus.recordable;

import io.littlehorse.quarkus.workflow.LHWorkflowConsumer;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHWorkflowConsumerRecordable implements LHWorkflowRecordable {

    private final String beanClassName;
    private final String wfSpecName;

    @RecordableConstructor
    public LHWorkflowConsumerRecordable(String beanClassName, String wfSpecName) {
        this.beanClassName = beanClassName;
        this.wfSpecName = wfSpecName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public String getWfSpecName() {
        return wfSpecName;
    }

    @Override
    public void getWorkflowThread(WorkflowThread workflowThread) {
        try {
            Class<?> beanClass =
                    Thread.currentThread().getContextClassLoader().loadClass(beanClassName);
            LHWorkflowConsumer bean =
                    (LHWorkflowConsumer) CDI.current().select(beanClass).get();
            bean.accept(workflowThread);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
