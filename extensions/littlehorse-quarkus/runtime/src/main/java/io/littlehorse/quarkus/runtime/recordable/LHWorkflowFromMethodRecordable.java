package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.runtime.annotations.RecordableConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LHWorkflowFromMethodRecordable extends LHWorkflowRecordable {

    private final String beanMethodName;

    @RecordableConstructor
    public LHWorkflowFromMethodRecordable(
            Class<?> beanClass,
            String name,
            String beanMethodName,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType) {
        super(beanClass, name, parent, defaultTaskTimeout, defaultTaskRetries, updateType);
        this.beanMethodName = beanMethodName;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    @Override
    public void buildWorkflowThread(WorkflowThread workflowThread) {
        try {
            Method method = getBeanClass().getMethod(getBeanMethodName(), WorkflowThread.class);
            method.invoke(getBean(), workflowThread);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
