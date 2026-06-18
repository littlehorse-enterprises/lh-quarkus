package io.littlehorse.quarkus.deployment.annotation;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.util.Optional;

public final class OptionalAnnotation {

    private final AnnotationInstance annotationInstance;

    public OptionalAnnotation(AnnotationInstance annotationInstance) {
        this.annotationInstance = annotationInstance;
    }

    public OptionalAnnotation getNested(String name) {
        return new OptionalAnnotation(Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asNested)
                .orElse(null));
    }

    public String getStringValue(String name) {
        return Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asString)
                .orElse(null);
    }

    public String getValue() {
        return this.getStringValue("value");
    }

    public String getClassValue(String name) {
        return Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asClass)
                .map(Object::toString)
                .orElse(null);
    }

    public String getEnumValue(String name) {
        return Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asEnum)
                .orElse(null);
    }

    public boolean isPreset() {
        return Optional.ofNullable(annotationInstance).isPresent();
    }
}
