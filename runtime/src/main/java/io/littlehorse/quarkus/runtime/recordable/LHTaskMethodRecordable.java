package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHTaskMethodRecordable {
    private final Class<?> beanClass;
    private final String taskDefName;

    @RecordableConstructor
    public LHTaskMethodRecordable(Class<?> beanClass, String taskDefName) {
        this.beanClass = beanClass;
        this.taskDefName = taskDefName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getTaskDefName() {
        return taskDefName;
    }

    public LHTaskWorker initTaskWorker() {
        LHConfig config = CDI.current().select(LHConfig.class).get();
        Object bean = CDI.current().select(beanClass).get();
        return new LHTaskWorker(bean, taskDefName, config);
    }
}
