package io.littlehorse.quarkus.deployment.reflection;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import java.util.Optional;

public final class LHWorkflowAnnotationDescriptor {
    private final String parent;
    private final String defaultTaskTimeout;
    private final String wfSpecName;
    private final String defaultTaskRetries;
    private final String updateType;
    private final String retentionAfterTermination;

    private LHWorkflowAnnotationDescriptor(
            String wfSpecName,
            String parent,
            String defaultTaskTimeout,
            String defaultTaskRetries,
            String updateType,
            String retentionAfterTermination) {
        this.parent = parent;
        this.defaultTaskTimeout = defaultTaskTimeout;
        this.wfSpecName = wfSpecName;
        this.defaultTaskRetries = defaultTaskRetries;
        this.updateType = updateType;
        this.retentionAfterTermination = retentionAfterTermination;
    }

    private static final class AnnotationHelper {

        private final AnnotationInstance annotationInstance;

        public AnnotationHelper(AnnotationInstance annotationInstance) {
            this.annotationInstance = annotationInstance;
        }

        private String extractValue(String name) {
            return Optional.ofNullable(annotationInstance.value(name))
                    .map(AnnotationValue::asString)
                    .orElse(null);
        }

        private String extractValue() {
            return annotationInstance.value().asString();
        }
    }

    public static LHWorkflowAnnotationDescriptor describe(AnnotationInstance annotationInstance) {
        AnnotationHelper annotationHelper = new AnnotationHelper(annotationInstance);

        return new LHWorkflowAnnotationDescriptor(
                annotationHelper.extractValue(),
                annotationHelper.extractValue("parent"),
                annotationHelper.extractValue("defaultTaskTimeout"),
                annotationHelper.extractValue("defaultTaskRetries"),
                annotationHelper.extractValue("updateType"),
                annotationHelper.extractValue("retentionAfterTermination"));
    }

    public String getWfSpecName() {
        return wfSpecName;
    }

    public String getParent() {
        return parent;
    }

    public String getDefaultTaskTimeout() {
        return defaultTaskTimeout;
    }

    public String getDefaultTaskRetries() {
        return defaultTaskRetries;
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getRetentionAfterTermination() {
        return retentionAfterTermination;
    }
}
