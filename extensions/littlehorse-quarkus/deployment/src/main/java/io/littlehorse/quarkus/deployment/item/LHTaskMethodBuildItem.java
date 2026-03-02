package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHTaskMethodDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHTaskMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHTaskMethodDescriptor descriptor;

    public LHTaskMethodBuildItem(Class<?> beanClass, LHTaskMethodDescriptor descriptor) {
        this.descriptor = descriptor;
        this.beanClass = beanClass;
    }

    public LHTaskMethodRecordable toRecordable() {
        return new LHTaskMethodRecordable(
                beanClass, descriptor.getTaskDefName(), descriptor.getDescription());
    }
}
