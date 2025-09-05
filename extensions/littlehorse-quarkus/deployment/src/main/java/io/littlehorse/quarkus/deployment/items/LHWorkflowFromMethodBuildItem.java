package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.deployment.reflection.LHWorkflowAnnotationDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowFromMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowFromMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String beanMethodName;
    private final String wfSpecName;
    private final String parent;
    private final String defaultTaskTimeout;
    private final String defaultTaskRetries;
    private final String updateType;
    private final String retentionAfterTermination;

    public LHWorkflowFromMethodBuildItem(
            Class<?> beanClass,
            String beanMethodName,
            String wfSpecName,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType,
            String retentionAfterTermination) {
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.wfSpecName = wfSpecName;
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
        this.retentionAfterTermination = retentionAfterTermination;
    }

    public LHWorkflowFromMethodBuildItem(
            Class<?> beanClass, String beanMethodName, LHWorkflowAnnotationDescriptor descriptor) {
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.wfSpecName = descriptor.getWfSpecName();
        this.parent = descriptor.getParent();
        this.defaultTaskTimeout = descriptor.getDefaultTaskTimeout();
        this.defaultTaskRetries = descriptor.getDefaultTaskRetries();
        this.updateType = descriptor.getUpdateType();
        this.retentionAfterTermination = descriptor.getRetentionAfterTermination();
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getRetentionAfterTermination() {
        return retentionAfterTermination;
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

    public String getBeanMethodName() {
        return beanMethodName;
    }

    public String getDefaultTaskTimeout() {
        return defaultTaskTimeout;
    }

    public LHWorkflowFromMethodRecordable toRecordable() {
        return new LHWorkflowFromMethodRecordable(
                beanClass,
                wfSpecName,
                beanMethodName,
                parent,
                defaultTaskTimeout,
                defaultTaskRetries,
                updateType,
                retentionAfterTermination);
    }
}
