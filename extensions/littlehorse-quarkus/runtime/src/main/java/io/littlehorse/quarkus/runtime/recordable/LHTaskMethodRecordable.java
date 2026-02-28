package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.runtime.register.LHTaskRegister;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHTaskMethodRecordable extends LHRecordable {

    private final String description;

    @RecordableConstructor
    public LHTaskMethodRecordable(Class<?> beanClass, String name, String description) {
        super(beanClass, name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void registerAndStartTask(ShutdownContext shutdownContext) {
        if (!exists()) return;

        ConfigEvaluator configEvaluator = new ConfigEvaluator();
        LHConfig config = CDI.current().select(LHConfig.class).get();
        LHTaskRegister taskRegister = CDI.current().select(LHTaskRegister.class).get();
        LHTaskWorker worker = new LHTaskWorker(
                bean(), getName(), config, configEvaluator.expand(getName()).getMembers());
        shutdownContext.addShutdownTask(new ShutdownContext.CloseRunnable(worker));
        taskRegister.registerAndStartTask(worker);
    }
}
