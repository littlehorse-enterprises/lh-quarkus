package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHUserTaskFormDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskFormRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHUserTaskFormBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHUserTaskFormDescriptor descriptor;

    public LHUserTaskFormBuildItem(Class<?> beanClass, LHUserTaskFormDescriptor descriptor) {
        this.descriptor = descriptor;
        this.beanClass = beanClass;
    }

    public LHUserTaskFormRecordable toRecordable() {
        return new LHUserTaskFormRecordable(beanClass, descriptor.getUserTaskDefName());
    }
}
