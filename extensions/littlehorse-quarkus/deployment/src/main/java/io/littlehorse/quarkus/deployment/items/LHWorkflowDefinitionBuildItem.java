package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.deployment.reflection.LHWorkflowAnnotationDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowDefinitionRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowDefinitionBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String wfSpecName;
    private final String parent;
    private final String defaultTaskTimeout;

    public LHWorkflowDefinitionBuildItem(
            Class<?> beanClass, String wfSpecName, String parent, String defaultTaskTimeout) {
        this.beanClass = beanClass;
        this.wfSpecName = wfSpecName;
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
    }

    public LHWorkflowDefinitionBuildItem(
            Class<?> beanClass, LHWorkflowAnnotationDescriptor descriptor) {
        this.beanClass = beanClass;
        this.wfSpecName = descriptor.wfSpecName();
        this.parent = descriptor.parent();
        this.defaultTaskTimeout = descriptor.defaultTaskTimeout();
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

    public String getDefaultTaskTimeout() {
        return defaultTaskTimeout;
    }

    public LHWorkflowDefinitionRecordable toRecordable() {
        return new LHWorkflowDefinitionRecordable(beanClass, wfSpecName, parent);
    }
}
