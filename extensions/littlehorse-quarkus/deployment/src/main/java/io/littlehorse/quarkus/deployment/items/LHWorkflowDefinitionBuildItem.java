package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.deployment.reflection.LHExponentialBackoffRetryDescriptor;
import io.littlehorse.quarkus.deployment.reflection.LHWorkflowDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHExponentialBackoffRetryRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowDefinitionRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowDefinitionBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final LHWorkflowDescriptor descriptor;

    public LHWorkflowDefinitionBuildItem(Class<?> beanClass, LHWorkflowDescriptor descriptor) {
        this.beanClass = beanClass;
        this.descriptor = descriptor;
    }

    public LHWorkflowDefinitionRecordable toRecordable() {
        LHExponentialBackoffRetryDescriptor backoffRetryDescriptor =
                descriptor.getDefaultTaskExponentialBackoffRetry();
        return new LHWorkflowDefinitionRecordable(
                beanClass,
                descriptor.getWfSpecName(),
                descriptor.getParent(),
                descriptor.getDefaultTaskTimeout(),
                descriptor.getDefaultTaskRetries(),
                descriptor.getUpdateType(),
                descriptor.getRetention(),
                descriptor.getDefaultThreadRetention(),
                backoffRetryDescriptor != null
                        ? new LHExponentialBackoffRetryRecordable(
                                backoffRetryDescriptor.getBaseIntervalMs(),
                                backoffRetryDescriptor.getMultiplier(),
                                backoffRetryDescriptor.getMaxDelayMs())
                        : null);
    }
}
