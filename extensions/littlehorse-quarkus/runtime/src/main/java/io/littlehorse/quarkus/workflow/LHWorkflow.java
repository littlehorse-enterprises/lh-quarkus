package io.littlehorse.quarkus.workflow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LHWorkflow {
    /**
     * Workflow name (WfSpec).
     */
    String value();

    /**
     * The name of the parent wfSpec.
     */
    String parent() default "";

    /**
     * Sets the default timeout in seconds for all TaskRun's in this workflow.
     */
    String defaultTaskTimeout() default "";
}
