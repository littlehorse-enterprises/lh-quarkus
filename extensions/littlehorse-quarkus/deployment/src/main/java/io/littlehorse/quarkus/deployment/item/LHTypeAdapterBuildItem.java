package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHTypeAdapterDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHTypeAdapterRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHTypeAdapterBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHTypeAdapterDescriptor descriptor;

    public LHTypeAdapterBuildItem(Class<?> beanClass, LHTypeAdapterDescriptor descriptor) {
        this.beanClass = beanClass;
        this.descriptor = descriptor;
    }

    public LHTypeAdapterRecordable toRecordable() {
        return new LHTypeAdapterRecordable(
                beanClass, descriptor.getAdaptedType(), descriptor.getVariableType());
    }
}
