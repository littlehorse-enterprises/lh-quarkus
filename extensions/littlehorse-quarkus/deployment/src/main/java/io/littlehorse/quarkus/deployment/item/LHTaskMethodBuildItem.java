package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHTaskMethodDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

import java.util.Set;

public final class LHTaskMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHTaskMethodDescriptor descriptor;
    private final Set<String> structDefNameTemplates;

    public LHTaskMethodBuildItem(Class<?> beanClass, LHTaskMethodDescriptor descriptor) {
        this(beanClass, descriptor, Set.of());
    }

    public LHTaskMethodBuildItem(
            Class<?> beanClass,
            LHTaskMethodDescriptor descriptor,
            Set<String> structDefNameTemplates) {
        this.descriptor = descriptor;
        this.beanClass = beanClass;
        this.structDefNameTemplates = Set.copyOf(structDefNameTemplates);
    }

    public LHTaskMethodRecordable toRecordable() {
        return new LHTaskMethodRecordable(
                beanClass,
                descriptor.getTaskDefName(),
                descriptor.getDescription(),
                structDefNameTemplates);
    }
}
