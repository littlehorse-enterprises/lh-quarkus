package io.littlehorse.quarkus.runtime.recordable;

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

    public Object getBean() {
        return CDI.current().select(beanClass).get();
    }
}
