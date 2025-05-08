package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LHWorkflowFromMethodRecordable implements LHWorkflowRecordable {

    private final Class<?> beanClass;
    private final String beanMethodName;
    private final String wfSpecName;

    @RecordableConstructor
    public LHWorkflowFromMethodRecordable(
            Class<?> beanClass, String beanMethodName, String wfSpecName) {
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.wfSpecName = wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    @Override
    public String getWfSpecName() {
        return wfSpecName;
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        try {
            Object bean = CDI.current().select(beanClass).get();
            Method method = beanClass.getMethod(beanMethodName, WorkflowThread.class);
            method.invoke(bean, workflowThread);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
