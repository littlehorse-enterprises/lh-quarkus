package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigExpression;
import io.littlehorse.quarkus.runtime.register.LHUserTaskRegister;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHUserTaskRecordable extends LHRecordable {

    @RecordableConstructor
    public LHUserTaskRecordable(Class<?> beanClass, String name) {
        super(beanClass, name);
    }

    public void registerUserTask() {
        if (!exists()) return;

        LHUserTaskRegister register =
                CDI.current().select(LHUserTaskRegister.class).get();
        register.registerUserTask(getBean(), ConfigExpression.expand(getName()).asString());
    }
}
