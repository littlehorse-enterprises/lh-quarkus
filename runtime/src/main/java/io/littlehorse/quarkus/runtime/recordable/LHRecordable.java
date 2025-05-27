package io.littlehorse.quarkus.runtime.recordable;

import jakarta.enterprise.inject.spi.CDI;

public abstract class LHRecordable {

    private final Class<?> beanClass;
    private final String name;

    public LHRecordable(Class<?> beanClass, String name) {
        this.beanClass = beanClass;
        this.name = name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getName() {
        return name;
    }

    public boolean doesExist() {
        return CDI.current().select(getBeanClass()).isResolvable();
    }

    public Object getBean() {
        return CDI.current().select(getBeanClass()).get();
    }
}
