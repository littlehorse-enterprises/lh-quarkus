package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowDefinitionRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowDefinitionBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String wfSpecName;

    public LHWorkflowDefinitionBuildItem(Class<?> beanClass, String wfSpecName) {
        this.wfSpecName = wfSpecName;
        this.beanClass = beanClass;
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public LHWorkflowDefinitionRecordable toRecordable() {
        return new LHWorkflowDefinitionRecordable(beanClass, wfSpecName);
    }
}
