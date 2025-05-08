package io.littlehorse.quarkus.deployment.items;

import io.quarkus.builder.item.MultiBuildItem;

public final class LHTaskMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String name;

    public LHTaskMethodBuildItem(Class<?> beanClass, String name) {
        this.name = name;
        this.beanClass = beanClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
}
