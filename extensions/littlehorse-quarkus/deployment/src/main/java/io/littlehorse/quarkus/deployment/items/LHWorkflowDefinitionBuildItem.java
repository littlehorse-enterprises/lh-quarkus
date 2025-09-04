package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowDefinitionRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowDefinitionBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String wfSpecName;
    private final String parent;

    public LHWorkflowDefinitionBuildItem(Class<?> beanClass, String wfSpecName, String parent) {
        this.wfSpecName = wfSpecName;
        this.beanClass = beanClass;
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

    public LHWorkflowDefinitionRecordable toRecordable() {
        return new LHWorkflowDefinitionRecordable(beanClass, wfSpecName, parent);
    }
}
