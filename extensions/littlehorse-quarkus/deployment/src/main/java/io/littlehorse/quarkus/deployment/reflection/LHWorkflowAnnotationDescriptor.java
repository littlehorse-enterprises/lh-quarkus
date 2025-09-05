package io.littlehorse.quarkus.deployment.reflection;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.util.Optional;

public final class LHWorkflowAnnotationDescriptor {
    private final String parent;
    private final String defaultTaskTimeout;
    private final String wfSpecName;

    private LHWorkflowAnnotationDescriptor(
            String wfSpecName, String parent, String defaultTaskTimeout) {
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.wfSpecName = wfSpecName;
    }

    public static LHWorkflowAnnotationDescriptor describe(AnnotationInstance annotationInstance) {
        String value = annotationInstance.value().asString();
        String parent = extractValue(annotationInstance, "parent");
        String defaultTaskTimeout = extractValue(annotationInstance, "defaultTaskTimeout");
        return new LHWorkflowAnnotationDescriptor(value, parent, defaultTaskTimeout);
    }

    private static String extractValue(AnnotationInstance annotationInstance, String name) {
        return Optional.ofNullable(annotationInstance.value(name))
                .map(AnnotationValue::asString)
                .orElse(null);
    }

    public String wfSpecName() {
        return wfSpecName;
    }

    public String parent() {
        return parent;
    }

    public String defaultTaskTimeout() {
        return defaultTaskTimeout;
    }
}
