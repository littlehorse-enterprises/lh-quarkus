package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHUserTaskFormDescriptor {

    private final OptionalAnnotation annotation;

    public LHUserTaskFormDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getUserTaskDefName() {
        return annotation.getValue();
    }
}
