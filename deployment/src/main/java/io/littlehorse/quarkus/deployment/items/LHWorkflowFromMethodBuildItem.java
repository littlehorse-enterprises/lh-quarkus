package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowFromMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowFromMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String beanMethodName;
    private final String wfSpecName;

    public LHWorkflowFromMethodBuildItem(
            Class<?> beanClass, String beanMethodName, String wfSpecName) {
        this.wfSpecName = wfSpecName;
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public LHWorkflowFromMethodRecordable toRecordable() {
        return new LHWorkflowFromMethodRecordable(beanClass, beanMethodName, wfSpecName);
    }
}
