package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHWorkflowDescriptor {

    private final OptionalAnnotation annotation;

    public LHWorkflowDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getWfSpecName() {
        return annotation.getValue();
    }

    public String getParent() {
        return annotation.getStringValue("parent");
    }

    public String getDefaultTaskTimeout() {
        return annotation.getStringValue("defaultTaskTimeout");
    }

    public String getDefaultTaskRetries() {
        return annotation.getStringValue("defaultTaskRetries");
    }

    public String getUpdateType() {
        return annotation.getStringValue("updateType");
    }

    public String getRetention() {
        return annotation.getStringValue("retention");
    }

    public String getDefaultThreadRetention() {
        return annotation.getStringValue("defaultThreadRetention");
    }

    public LHExponentialBackoffRetryDescriptor getDefaultTaskExponentialBackoffRetry() {
        OptionalAnnotation backoffRetryAnnotation =
                annotation.getNested("defaultTaskExponentialBackoffRetry");
        return backoffRetryAnnotation.isPreset()
                ? new LHExponentialBackoffRetryDescriptor(backoffRetryAnnotation)
                : null;
    }
}
