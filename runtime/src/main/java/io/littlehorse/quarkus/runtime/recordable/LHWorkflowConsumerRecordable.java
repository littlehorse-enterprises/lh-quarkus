package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.workflow.LHWorkflowConsumer;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHWorkflowConsumerRecordable implements LHWorkflowRecordable {

    private final Class<?> beanClass;
    private final String wfSpecName;

    @RecordableConstructor
    public LHWorkflowConsumerRecordable(Class<?> beanClass, String wfSpecName) {
        this.beanClass = beanClass;
        this.wfSpecName = wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public String getWfSpecName() {
        return wfSpecName;
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        LHWorkflowConsumer bean =
                (LHWorkflowConsumer) CDI.current().select(beanClass).get();
        bean.accept(workflowThread);
    }
}
