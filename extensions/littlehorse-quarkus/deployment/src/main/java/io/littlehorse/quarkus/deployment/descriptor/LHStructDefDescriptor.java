package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHStructDefDescriptor {

    private final OptionalAnnotation annotation;

    public LHStructDefDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getStructDefName() {
        return annotation.getValue();
    }

    public String getDescription() {
        return annotation.getValue("description");
    }
}
