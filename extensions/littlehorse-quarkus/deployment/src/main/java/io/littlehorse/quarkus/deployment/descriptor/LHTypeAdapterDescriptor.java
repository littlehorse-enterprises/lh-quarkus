package io.littlehorse.quarkus.deployment.descriptor;

import io.littlehorse.quarkus.deployment.annotation.OptionalAnnotation;
import io.littlehorse.quarkus.deployment.util.ClassLoadingUtils;
import io.littlehorse.sdk.common.proto.VariableType;

public final class LHTypeAdapterDescriptor {

    private final OptionalAnnotation annotation;

    public LHTypeAdapterDescriptor(OptionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public String getAdaptedTypeName() {
        return annotation.getClassValue("adaptedType");
    }

    public Class<?> getAdaptedType() {
        return ClassLoadingUtils.loadClass(getAdaptedTypeName());
    }

    public VariableType getVariableType() {
        return VariableType.valueOf(annotation.getEnumValue("variableType"));
    }
}
