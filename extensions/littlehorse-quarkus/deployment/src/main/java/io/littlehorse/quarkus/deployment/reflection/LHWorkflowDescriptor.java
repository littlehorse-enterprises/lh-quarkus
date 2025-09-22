package io.littlehorse.quarkus.deployment.reflection;

public final class LHWorkflowDescriptor {

    private final OptionalAnnotation annotation;

    public LHWorkflowDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getWfSpecName() {
        return annotation.getValue();
    }

    public String getParent() {
        return annotation.getValue("parent");
    }

    public String getDefaultTaskTimeout() {
        return annotation.getValue("defaultTaskTimeout");
    }

    public String getDefaultTaskRetries() {
        return annotation.getValue("defaultTaskRetries");
    }

    public String getUpdateType() {
        return annotation.getValue("updateType");
    }

    public String getRetention() {
        return annotation.getValue("retention");
    }

    public String getDefaultThreadRetention() {
        return annotation.getValue("defaultThreadRetention");
    }

    public LHExponentialBackoffRetryDescriptor getDefaultTaskExponentialBackoffRetry() {
        OptionalAnnotation backoffRetryAnnotation =
                annotation.getNested("defaultTaskExponentialBackoffRetry");
        return backoffRetryAnnotation.isPreset()
                ? new LHExponentialBackoffRetryDescriptor(backoffRetryAnnotation)
                : null;
    }
}
