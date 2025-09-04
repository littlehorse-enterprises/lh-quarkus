package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHWorkflowDefinitionRecordable extends LHWorkflowRecordable {

    @RecordableConstructor
    public LHWorkflowDefinitionRecordable(Class<?> beanClass, String name, String parent) {
        super(beanClass, name, parent);
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        LHWorkflowDefinition bean =
                (LHWorkflowDefinition) CDI.current().select(getBeanClass()).get();
        bean.define(workflowThread);
    }
}
