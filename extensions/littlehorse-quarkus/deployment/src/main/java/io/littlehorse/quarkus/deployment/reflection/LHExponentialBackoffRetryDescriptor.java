package io.littlehorse.quarkus.deployment.reflection;

public final class LHExponentialBackoffRetryDescriptor {

    private final OptionalAnnotation annotation;

    public LHExponentialBackoffRetryDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getBaseIntervalMs() {
        return annotation.getValue("baseIntervalMs");
    }

    public String getMultiplier() {
        return annotation.getValue("multiplier");
    }

    public String getMaxDelayMs() {
        return annotation.getValue("maxDelayMs");
    }
}
