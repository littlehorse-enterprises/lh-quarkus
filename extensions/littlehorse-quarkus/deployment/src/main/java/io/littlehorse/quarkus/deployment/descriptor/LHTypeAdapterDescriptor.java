package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.sdk.common.proto.VariableType;

import org.jboss.jandex.AnnotationInstance;

public final class LHTypeAdapterDescriptor {

    private final AnnotationInstance annotation;

    public LHTypeAdapterDescriptor(AnnotationInstance annotation) {
        this.annotation = annotation;
    }

    public String getAdaptedTypeName() {
        return annotation.value("adaptedType").asClass().toString();
    }

    public VariableType getVariableType() {
        return VariableType.valueOf(annotation.value("variableType").asEnum());
    }
}
