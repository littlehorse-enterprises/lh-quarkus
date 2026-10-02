package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHUserTaskDefDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHUserTaskDefRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHUserTaskDefBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String resultStructDefName;
    private final LHUserTaskDefDescriptor descriptor;

    public LHUserTaskDefBuildItem(
            Class<?> beanClass, LHUserTaskDefDescriptor descriptor, String resultStructDefName) {
        this.descriptor = descriptor;
        this.beanClass = beanClass;
        this.resultStructDefName = resultStructDefName;
    }

    public LHUserTaskDefRecordable toRecordable() {
        return new LHUserTaskDefRecordable(
                beanClass, descriptor.getUserTaskDefName(), resultStructDefName);
    }
}
