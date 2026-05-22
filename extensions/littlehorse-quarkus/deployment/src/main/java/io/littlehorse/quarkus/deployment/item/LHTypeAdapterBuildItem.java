package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHTypeAdapterDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHTypeAdapterRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHTypeAdapterBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final Class<?> adaptedType;
    private final LHTypeAdapterDescriptor descriptor;

    public LHTypeAdapterBuildItem(
            Class<?> beanClass, Class<?> adaptedType, LHTypeAdapterDescriptor descriptor) {
        this.beanClass = beanClass;
        this.adaptedType = adaptedType;
        this.descriptor = descriptor;
    }

    public LHTypeAdapterRecordable toRecordable() {
        return new LHTypeAdapterRecordable(beanClass, adaptedType, descriptor.getVariableType());
    }
}
