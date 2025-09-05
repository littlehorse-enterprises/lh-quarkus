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
        String parent = Optional.ofNullable(annotationInstance.value("parent"))
                .map(AnnotationValue::asString)
                .orElse(null);
        String defaultTaskTimeout = Optional.ofNullable(
                        annotationInstance.value("defaultTaskTimeout"))
                .map(AnnotationValue::asString)
                .orElse(null);
        return new LHWorkflowAnnotationDescriptor(value, parent, defaultTaskTimeout);
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
