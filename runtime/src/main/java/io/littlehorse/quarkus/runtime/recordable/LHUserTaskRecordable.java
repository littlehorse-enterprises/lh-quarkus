package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.runtime.LHUserTaskRegister;
import io.quarkus.runtime.annotations.RecordableConstructor;

import jakarta.enterprise.inject.spi.CDI;

public class LHUserTaskRecordable {

    private final Class<?> beanClass;
    private final String userTaskDefName;

    @RecordableConstructor
    public LHUserTaskRecordable(Class<?> beanClass, String userTaskDefName) {
        this.beanClass = beanClass;
        this.userTaskDefName = userTaskDefName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getUserTaskDefName() {
        return userTaskDefName;
    }

    public void registerUserTask() {
        Object bean = CDI.current().select(beanClass).get();
        LHUserTaskRegister register =
                CDI.current().select(LHUserTaskRegister.class).get();
        register.registerUserTask(bean, userTaskDefName);
    }
}
