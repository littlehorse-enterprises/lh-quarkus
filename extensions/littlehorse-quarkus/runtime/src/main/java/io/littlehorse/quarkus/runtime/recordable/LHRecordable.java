package io.littlehorse.quarkus.runtime.recordable;

import io.littlehorse.quarkus.config.ConfigEvaluator;

import jakarta.enterprise.inject.spi.CDI;

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

    /**
     * Resolves the recordable's name as a configuration expression. This method must be executed
     * inside a recorder to avoid build time errors
     *
     * @return the expanded name of the recordable
     */
    public String getExpandedName() {
        ConfigEvaluator configEvaluator = getBean(ConfigEvaluator.class);
        return configEvaluator.expand(getName()).asString();
    }

    /**
     * Returns the names of other recordables that this one depends on. This method must be executed
     * inside a recorder to avoid build time errors
     *
     * @return a set of resolved dependency names
     */
    public Set<String> dependencies() {
        return Set.of();
    }

    protected static <T> T getBean(Class<T> beanClass) {
        return CDI.current().select(beanClass).get();
    }

    protected static <T> boolean doesBeanExist(Class<T> beanClass) {
        return CDI.current().select(beanClass).isResolvable();
    }
}
