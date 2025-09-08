package io.littlehorse.quarkus.deployment.items;

import io.littlehorse.quarkus.deployment.reflection.LHExponentialBackoffRetryDescriptor;
import io.littlehorse.quarkus.deployment.reflection.LHWorkflowDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHExponentialBackoffRetryRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowFromMethodRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowFromMethodBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String beanMethodName;
    private final LHWorkflowDescriptor descriptor;

    public LHWorkflowFromMethodBuildItem(
            Class<?> beanClass, String beanMethodName, LHWorkflowDescriptor descriptor) {
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.descriptor = descriptor;
    }

    public LHWorkflowFromMethodRecordable toRecordable() {
        LHExponentialBackoffRetryDescriptor backoffRetryDescriptor =
                descriptor.getDefaultTaskExponentialBackoffRetry();
        return new LHWorkflowFromMethodRecordable(
                beanClass,
                descriptor.getWfSpecName(),
                beanMethodName,
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
