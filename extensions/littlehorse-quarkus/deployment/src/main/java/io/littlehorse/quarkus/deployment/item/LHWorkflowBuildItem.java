package io.littlehorse.quarkus.deployment.item;

import io.littlehorse.quarkus.deployment.descriptor.LHExponentialBackoffRetryDescriptor;
import io.littlehorse.quarkus.deployment.descriptor.LHWorkflowDescriptor;
import io.littlehorse.quarkus.runtime.recordable.LHExponentialBackoffRetryRecordable;
import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;
import io.quarkus.builder.item.MultiBuildItem;

public final class LHWorkflowBuildItem extends MultiBuildItem {
    private final Class<?> beanClass;
    private final String beanMethodName;
    private final LHWorkflowDescriptor descriptor;

    public LHWorkflowBuildItem(
            Class<?> beanClass, String beanMethodName, LHWorkflowDescriptor descriptor) {
        this.beanClass = beanClass;
        this.beanMethodName = beanMethodName;
        this.descriptor = descriptor;
    }

    public LHWorkflowRecordable toRecordable() {
        LHExponentialBackoffRetryDescriptor backoffRetryDescriptor =
                descriptor.getDefaultTaskExponentialBackoffRetry();
        return new LHWorkflowRecordable(
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
