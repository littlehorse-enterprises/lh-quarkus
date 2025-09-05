package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.deployment.reflection.LHWorkflowAnnotationDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowDefinitionRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowDefinitionBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String wfSpecName;
    private final String parent;
    private final String defaultTaskTimeout;
    private final String defaultTaskRetries;
    private final String updateType;

    public LHWorkflowDefinitionBuildItem(
            Class<?> beanClass,
            String wfSpecName,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType) {
        this.beanClass = beanClass;
        this.wfSpecName = wfSpecName;
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
    }

    public LHWorkflowDefinitionBuildItem(
            Class<?> beanClass, LHWorkflowAnnotationDescriptor descriptor) {
        this.beanClass = beanClass;
        this.wfSpecName = descriptor.getWfSpecName();
        this.parent = descriptor.getParent();
        this.defaultTaskTimeout = descriptor.getDefaultTaskTimeout();
        this.defaultTaskRetries = descriptor.getDefaultTaskRetries();
        this.updateType = descriptor.getUpdateType();
    }

    public String getDefaultTaskRetries() {
        return defaultTaskRetries;
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

    public String getUpdateType() {
        return updateType;
    }

    public LHWorkflowDefinitionRecordable toRecordable() {
        return new LHWorkflowDefinitionRecordable(
                beanClass, wfSpecName, parent, defaultTaskTimeout, defaultTaskRetries, updateType);
    }
}
