package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHExponentialBackoffRetryDescriptor {

    private final OptionalAnnotation annotation;

    public LHExponentialBackoffRetryDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getBaseIntervalMs() {
        return annotation.getStringValue("baseIntervalMs");
    }

    public String getMultiplier() {
        return annotation.getStringValue("multiplier");
    }

    public String getMaxDelayMs() {
        return annotation.getStringValue("maxDelayMs");
    }
}
