package io.littlehorse.quarkus.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

import java.lang.reflect.Method;

public class LHWorkflowFromMethodRecordable {

    private final String beanClassName;
    private final String beanMethodName;
    private final String wfSpecName;

    @RecordableConstructor
    public LHWorkflowFromMethodRecordable(
            String beanClassName, String beanMethodName, String wfSpecName) {
        this.beanClassName = beanClassName;
        this.beanMethodName = beanMethodName;
        this.wfSpecName = wfSpecName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    /**
     * Invoke it only from the recorder class
     *
     * @param workflowThread Invoke it inside a ThreadFunc
     */
    public void invokeMethod(WorkflowThread workflowThread) {
        try {
            Class<?> beanClass =
                    Thread.currentThread().getContextClassLoader().loadClass(beanClassName);
            Object bean = CDI.current().select(beanClass).get();
            Method method = beanClass.getMethod(beanMethodName, WorkflowThread.class);
            method.invoke(bean, workflowThread);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
