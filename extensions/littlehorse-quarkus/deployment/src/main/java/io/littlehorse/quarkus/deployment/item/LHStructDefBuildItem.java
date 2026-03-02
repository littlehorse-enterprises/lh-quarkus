package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHStructDefDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHStructDefRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHStructDefBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHStructDefDescriptor descriptor;

    public LHStructDefBuildItem(Class<?> beanClass, LHStructDefDescriptor descriptor) {
        this.beanClass = beanClass;
        this.descriptor = descriptor;
    }

    public LHStructDefRecordable toRecordable() {
        return new LHStructDefRecordable(
                beanClass, descriptor.getStructDefName(), descriptor.getDescription());
    }
}
