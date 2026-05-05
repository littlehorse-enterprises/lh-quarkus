package io.littlehorse.quarkus.runtime.recordable;

import java.util.Set;

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

    public Set<String> dependencies() {
        return Set.of();
    }
}
