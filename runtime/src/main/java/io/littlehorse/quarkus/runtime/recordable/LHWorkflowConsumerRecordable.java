package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.workflow.LHWorkflowConsumer;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHWorkflowConsumerRecordable extends LHWorkflowRecordable {

    @RecordableConstructor
    public LHWorkflowConsumerRecordable(Class<?> beanClass, String wfSpecName) {
        super(beanClass, wfSpecName);
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        LHWorkflowConsumer bean =
                (LHWorkflowConsumer) CDI.current().select(getBeanClass()).get();
        bean.accept(workflowThread);
    }
}
