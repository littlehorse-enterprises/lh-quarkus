package io.littlehorse.quarkus.deployment.annotation;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.lang.annotation.Annotation;
import java.util.Optional;

public final class OptionalAnnotation {

    private final AnnotationInstance annotationInstance;

    public OptionalAnnotation(Class<? extends Annotation> annotationType) {
        this.annotationInstance = AnnotationInstance.builder(annotationType).build();
    }

    public OptionalAnnotation(AnnotationInstance annotationInstance) {
        this.annotationInstance = annotationInstance;
    }

    public OptionalAnnotation getNested(String name) {
        return new OptionalAnnotation(Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asNested)
                .orElse(null));
    }

    public String getValue(String name) {
        return Optional.ofNullable(annotationInstance)
                .map(annotationInstance -> annotationInstance.value(name))
                .map(AnnotationValue::asString)
                .orElse(null);
    }

    public String getValue() {
        return this.getValue("value");
    }

    public boolean isPreset() {
        return Optional.ofNullable(annotationInstance).isPresent();
    }
}
