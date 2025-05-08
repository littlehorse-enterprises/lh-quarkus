package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowConsumerRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowConsumerBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String wfSpecName;

    public LHWorkflowConsumerBuildItem(Class<?> beanClass, String wfSpecName) {
        this.wfSpecName = wfSpecName;
        this.beanClass = beanClass;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public LHWorkflowConsumerRecordable toRecordable() {
        return new LHWorkflowConsumerRecordable(beanClass, wfSpecName);
    }
}
