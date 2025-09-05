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
     * Sets the default timeout in seconds (as int) for all TaskRun's in this workflow.
     */
    String defaultTaskTimeout() default "";

    /**
     * Tells the Workflow to configure (by default) a Simple Retry Policy for every Task Node.
     * Passing a value of '1' means that there will be one retry upon failure.
     * Retries are scheduled immediately without delay.
     * Can be overridden by setting the retry policy on the WorkflowThread or TaskNodeOutput level.
     * The number of retries to attempt (as int).
     */
    String defaultTaskRetries() default "";

    /**
     * Defines the type of update to perform when saving the WfSpec:
     * ALL_UPDATES (Default): Creates a new WfSpec with a different version (either major or revision).
     * MINOR_REVISION_UPDATES: Creates a new WfSpec with a different revision if the change is a major version it fails.
     * NO_UPDATES: Fail with the ALREADY_EXISTS response code.
     */
    String updateType() default "";

    /**
     * Delete all WfRun's X seconds (as int) after they terminate, regardless of status.
     */
    String retention() default "";

    /**
     * Delete associated ThreadRun's X seconds (as int) after they terminate, regardless of status.
     */
    String defaultThreadRetention() default "";
}
