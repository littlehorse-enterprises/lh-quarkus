package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowFromMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowFromMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String beanMethodName;
    private final String wfSpecName;
    private final String parent;

    public LHWorkflowFromMethodBuildItem(
            Class<?> beanClass, String beanMethodName, String wfSpecName, String parent) {
        this.wfSpecName = wfSpecName;
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.parent = parent;
    }

    public String getParent() {
        return parent;
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
        return new LHWorkflowFromMethodRecordable(beanClass, wfSpecName, beanMethodName, parent);
    }
}
