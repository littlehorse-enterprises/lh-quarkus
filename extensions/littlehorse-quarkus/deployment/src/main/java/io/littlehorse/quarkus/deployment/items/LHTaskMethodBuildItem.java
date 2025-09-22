package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHTaskMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHTaskMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String taskDefName;

    public LHTaskMethodBuildItem(Class<?> beanClass, String taskDefName) {
        this.taskDefName = taskDefName;
        this.beanClass = beanClass;
    }

    public LHTaskMethodRecordable toRecordable() {
        return new LHTaskMethodRecordable(beanClass, taskDefName);
    }
}
