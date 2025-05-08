package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LHWorkflowFromMethodRecordable extends LHWorkflowRecordable {

    private final String beanMethodName;

    @RecordableConstructor
    public LHWorkflowFromMethodRecordable(
            Class<?> beanClass, String beanMethodName, String wfSpecName) {
        super(beanClass, wfSpecName);
        this.beanMethodName = beanMethodName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        try {
            Object bean = CDI.current().select(getBeanClass()).get();
            Method method = getBeanClass().getMethod(beanMethodName, WorkflowThread.class);
            method.invoke(bean, workflowThread);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
