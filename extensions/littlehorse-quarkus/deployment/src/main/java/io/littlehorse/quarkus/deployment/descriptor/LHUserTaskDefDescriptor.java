package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;

public final class LHUserTaskDefDescriptor {

    private final OptionalAnnotation annotation;

    public LHUserTaskDefDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getResultClassName() {
        return annotation.getClassValue("result");
    }

    public String getUserTaskDefName() {
        return annotation.getValue();
    }
}
