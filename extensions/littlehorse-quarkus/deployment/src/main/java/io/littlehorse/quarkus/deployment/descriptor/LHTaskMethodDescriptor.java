package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHTaskMethodDescriptor {

    private final OptionalAnnotation annotation;

    public LHTaskMethodDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getTaskDefName() {
        return annotation.getValue();
    }

    public String getDescription() {
        return annotation.getValue("description");
    }
}
