package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHTaskMethodDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

import java.util.List;

public final class LHTaskMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHTaskMethodDescriptor descriptor;
    private final List<String> structDefNameExpressions;

    public LHTaskMethodBuildItem(Class<?> beanClass, LHTaskMethodDescriptor descriptor) {
        this(beanClass, descriptor, List.of());
    }

    public LHTaskMethodBuildItem(
            Class<?> beanClass,
            LHTaskMethodDescriptor descriptor,
            List<String> structDefNameExpressions) {
        this.descriptor = descriptor;
        this.beanClass = beanClass;
        this.structDefNameExpressions = List.copyOf(structDefNameExpressions);
    }

    public LHTaskMethodRecordable toRecordable() {
        return new LHTaskMethodRecordable(
                beanClass,
                descriptor.getTaskDefName(),
                descriptor.getDescription(),
                structDefNameExpressions);
    }
}
