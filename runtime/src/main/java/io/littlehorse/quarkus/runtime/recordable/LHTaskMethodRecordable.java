package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.runtime.LHTaskWorkersContainer;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.ShutdownContext;
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

    public void startTaskWorker(ShutdownContext shutdownContext) {
        LHConfig config = CDI.current().select(LHConfig.class).get();
        LHTaskWorkersContainer workerContainer =
                CDI.current().select(LHTaskWorkersContainer.class).get();
        Object bean = CDI.current().select(beanClass).get();
        LHTaskWorker worker = new LHTaskWorker(bean, taskDefName, config);
        shutdownContext.addShutdownTask(new ShutdownContext.CloseRunnable(worker));
        workerContainer.startTaskWorker(worker);
    }
}
