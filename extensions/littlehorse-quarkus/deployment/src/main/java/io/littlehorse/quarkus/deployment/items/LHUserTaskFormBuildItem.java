package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHUserTaskRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHUserTaskFormBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String userTaskDefName;

    public LHUserTaskFormBuildItem(Class<?> beanClass, String userTaskDefName) {
        this.userTaskDefName = userTaskDefName;
        this.beanClass = beanClass;
    }

    public String getUserTaskDefName() {
        return userTaskDefName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public LHUserTaskRecordable toRecordable() {
        return new LHUserTaskRecordable(beanClass, userTaskDefName);
    }
}
