package io.littlehorse.quarkus.runtime.recordable;

import jakarta.enterprise.inject.spi.CDI;

import java.util.List;

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

    // TODO REMOVE
    public boolean exists() {
        return CDI.current().select(getBeanClass()).isResolvable();
    }

    public Object bean() {
        return CDI.current().select(getBeanClass()).get();
    }

    public List<String> dependencies() {
        return List.of();
    }
}
